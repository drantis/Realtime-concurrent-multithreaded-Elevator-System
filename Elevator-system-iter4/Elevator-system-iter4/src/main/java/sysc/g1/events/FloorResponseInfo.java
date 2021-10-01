package sysc.g1.events;

import sysc.g1.utils.Direction;

/**
 * Represent information from scheduler and elevator to the floor system
 */
public class FloorResponseInfo extends EventInfo {

  private Direction direction;
  private int floor;
  private int workingElevator;

  private FloorResponseInfo(EventType floorResponseType, Direction direction, int floor) {
    this.type = floorResponseType;
    this.direction = direction;
    this.floor = floor;
    this.workingElevator = 0;
  }

  public FloorResponseInfo(EventType floorResponseType, Direction direction, int floor, int workingElevator) {
    this(floorResponseType, direction, floor);
    this.workingElevator = workingElevator;
  }

  public int getWorkingElevator() {
    return workingElevator;
  }

  public Direction getDirection() {
    return direction;
  }

  public int getFloor() {
    return floor;
  }

  public String toString() {
    return String.format("Event: %s, Direction: %s, Floor %d", type, direction, floor);
  }
}
