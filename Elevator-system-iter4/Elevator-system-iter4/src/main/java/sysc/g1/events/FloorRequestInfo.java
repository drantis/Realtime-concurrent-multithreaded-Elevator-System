package sysc.g1.events;

import sysc.g1.utils.Direction;

/**
 * Represent information of an event happens in the Floor subsystem
 */
public class FloorRequestInfo extends  EventInfo {
  private Direction direction;
  private int from;

  /**
   * Constructor
   * @param direction the direction that the passenger wants to move
   * @param from the floor number where the event happens
   */
  public FloorRequestInfo(Direction direction, int from) {
    type = EventType.FLOOR_REQUEST;
    this.direction = direction;
    this.from = from;
  }

  public Direction getDirection() {
    return direction;
  }

  public int getFrom() {
    return from;
  }

  public String toString() {
    return String.format("Passenger from floor %d request to go %s.", from, direction);
  }
}
