package sysc.g1.utils;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import sysc.g1.events.Event;

/**
 * A priority for for each thread to prioritize and process incoming events
 *
 * - The urgent events will be prioritized first
 * - If there are multiple urgent events or no urgent events, the queue will prioritize the event
 * that happens earlier (based on timestamp)
 *
 * Note that this class is thread-safe
 */
public class EventQueue {

  private static final Comparator<Event> COMPARATOR = new Comparator<Event>() {
    @Override
    public int compare(Event o1, Event o2) {
      if (o1.isUrgent() == o2.isUrgent()) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
      }

      return o1.isUrgent() ? -1 : 1;
    }
  };
  private Queue<Event> queue;

  public EventQueue() {
    this.queue = new PriorityBlockingQueue<>(100, COMPARATOR);
  }

  /**
   * Add an event to the queue
   */
  public void add(Event event) {
    queue.add(event);
  }

  /**
   * Get the next event to process
   */
  public Event next() {
    return queue.poll();
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  /**
   * Remove an event in the queue
   */
  public boolean remove(Event event) {
    return queue.remove(event);
  }

  public int size() {
    return queue.size();
  }

  public static void main(String[] args) {
    EventQueue queue = new EventQueue();
    Event e2 = new Event(LocalTime.now(), true);
    Event e1 = new Event(LocalTime.MIDNIGHT, true);
    queue.add(e1);
    queue.add(e2);

    System.out.println(queue.next().getTimestamp());
    System.out.println(queue.next().getTimestamp());
  }
}
