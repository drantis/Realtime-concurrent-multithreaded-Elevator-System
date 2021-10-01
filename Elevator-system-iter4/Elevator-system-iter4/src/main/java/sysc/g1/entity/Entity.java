package sysc.g1.entity;

import java.time.Duration;
import java.time.LocalTime;
import sysc.g1.events.Event;
import sysc.g1.events.HandShakeInfo;
import sysc.g1.utils.ConsoleLogger;
import sysc.g1.utils.EventQueue;

public abstract class Entity implements Runnable {
  protected final EntityType entityType;
  protected String id;
  protected ConsoleLogger logger;
  protected boolean shouldStop;
  private LocalTime realStartTime;
  private LocalTime clockStartTime;
  private boolean handShaked;

  private EventQueue eventQueue;

  public Entity(EntityType entityType, String id) {
    this.id = id;
    this.entityType = entityType;
    this.logger = ConsoleLogger.getInstance();
    this.eventQueue = new EventQueue();
    this.handShaked = false;
  }

  public String getId() {
    return id;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public int pendingEvents() {
    return eventQueue.size();
  }

  //////////////////////
  // RECEIVING EVENTS //
  //////////////////////
  public synchronized void receiveEvent(Event event) {
    if (handShaked) {
      display(getCurrentClockTime(), String.format("Event Received: %s", event));
    }
    eventQueue.add(event);
    notifyAll();
  }

  protected abstract void processEvent(Event event);

  public void display(LocalTime time, String message) {
    logger.log(String.format("%s - %s: %s", time.toString(), id, message));
  }

  public synchronized void stop() {
    this.shouldStop = true;
  }

  public synchronized boolean isStopping() {
    return this.shouldStop;
  }

  private void handShake() {
    while (eventQueue.isEmpty()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
//    System.out.println(eventQueue.size());
    Event handShake = eventQueue.next();

    if (!(handShake.getInfo() instanceof HandShakeInfo)) {
      System.out.println("First event must be a hand shake event");
      System.exit(1);
    }
    HandShakeInfo info = (HandShakeInfo) handShake.getInfo();
    realStartTime = info.getRealStartupTime();
    clockStartTime = info.getStartupTimestamp();
    handShaked = true;
  }

  public LocalTime getCurrentClockTime() {
    if (realStartTime == null) {
      return LocalTime.now();
    }
    LocalTime now = LocalTime.now().withNano(0);
    Duration offset = Duration.between(realStartTime, now);
    return clockStartTime.plus(offset);
  }

  @Override
  public void run() {
    handShake();
    while (!isStopping()) {
      while (eventQueue.isEmpty()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      Event event = eventQueue.next();
      event.setTime(getCurrentClockTime());
      processEvent(event);
    }
  }
}
