package sysc.g1.actors;

import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;
import static sysc.g1.network.SubsystemPorts.FLOOR_IP;
import static sysc.g1.network.SubsystemPorts.FLOOR_PORT;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_IP;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_PORT;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorRequestInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventInfo;
import sysc.g1.events.EventUtil;
import sysc.g1.events.HandShakeInfo;
import sysc.g1.floor.Floor;
import sysc.g1.network.NetworkService;
import sysc.g1.scheduler.SchedulerSubsystem;

/** Simulates a passenger that performs several actions/events interacting with the system */
public class ActorSimulator implements Runnable {

  private NetworkService networkService;

  public ActorSimulator(InetAddress address, int port) {
    this.networkService = new NetworkService(null, port, address);
  }

  @Override
  public void run() {
    List<Event> events = new EventUtil().readEvents();
    long time;
    long currentSystemTime;
    long initialTime = (Time.valueOf(events.get(0).getTimestamp())).getTime();
    LocalTime initTime = events.get(0).getTimestamp();
    LocalTime systemTime = LocalTime.now().withNano(0);
    networkService.send(getHandShakeEvent(initTime, systemTime, Floor.getTargetId(0)), FLOOR_IP, FLOOR_PORT);
    networkService.send(getHandShakeEvent(initTime, systemTime, SchedulerSubsystem.TARGET_ID), SCHEDULER_IP, SCHEDULER_PORT);
    networkService.send(getHandShakeEvent(initTime, systemTime,  Elevator.getTargetId(0)), ELEVATOR_IP, ELEVATOR_PORT);

    for (Event ev : events) {
      long initialSystemTime = Time.valueOf(systemTime).getTime();
      time = Time.valueOf(ev.getTimestamp()).getTime();

      try {
        currentSystemTime = Time.valueOf(LocalTime.now()).getTime();
        long elapsedTime = currentSystemTime - initialSystemTime;
        LocalTime elevatorTimeStamp = ev.getTimestamp().plusNanos(elapsedTime * 1000000);
        Thread.sleep(time - initialTime);
        if (ev.getInfo() instanceof ElevatorRequestInfo) {
          // Calculating time spent in-between communication from Floor -> Scheduler -> Elevator
          Event elevatorEvent =
              new Event(
                  elevatorTimeStamp,
                  EntityType.ACTOR,
                  ev.getInfo(),
                  "actor",
                  Elevator.getTargetId(1),
                  false);

          // Send button press event to both
          networkService.send(ev, FLOOR_IP, FLOOR_PORT);
          networkService.send(elevatorEvent, ELEVATOR_IP, ELEVATOR_PORT);
        } else {
          // If the event is an error event
          networkService.send(ev, ELEVATOR_IP, ELEVATOR_PORT);
        }
        System.out.println(time - initialTime);
        initialTime = Time.valueOf(ev.getTimestamp()).getTime();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Create a handshake event so that all entity can agree on a starting time
   * @param initTime
   * @return
   */
  private Event getHandShakeEvent(LocalTime initTime, LocalTime systemTime, String targetId) {
    EventInfo info = new HandShakeInfo(initTime, systemTime);
    return new Event(initTime, EntityType.ACTOR, info, "actor", targetId, true);
  }

  public static void main(String[] args) throws UnknownHostException {
    ActorSimulator actor = new ActorSimulator(InetAddress.getLocalHost(), 23423);
    actor.run();
  }
}
