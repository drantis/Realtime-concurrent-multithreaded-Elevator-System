package sysc.g1.events;

import sysc.g1.utils.Direction;

/**
 * This class represents an event request sent specifically to the elevator
 * 
 */
public class ElevatorRequestInfo extends EventInfo {

  private int fromFloor;
  private int toFloor;
  private Direction direction;
  private int elevatorId;

  
  /**
	 * Class constructor
	 * 
	 * @param fromFloor the request floor of the elevator
	 * @param toFloor the destination floor of the elevator
	 * @param direction the direction the elevator is travelling in i.e. up or down
	 */
  public ElevatorRequestInfo(int elevatorId, int fromFloor, int toFloor, Direction direction) {
    this.fromFloor = fromFloor;
    this.toFloor = toFloor;
    this.direction = direction;
    this.type = EventType.ELEVATOR_REQUEST;
    this.elevatorId = elevatorId;
  }

  public ElevatorRequestInfo(int elevatorId, int fromFloor, int toFloor, Direction direction, EventType type) {
    this.fromFloor = fromFloor;
    this.toFloor = toFloor;
    this.direction = direction;
    this.type = type;
    this.elevatorId = elevatorId;
  }

  public int getElevatorId() {
    return elevatorId;
  }

  public int getToFloor() {
    return toFloor;
  }

  public int getFromFloor() {
    return fromFloor;
  }

  public Direction getDirection() {
    return direction;
  }
  
  public String toString() {
    return String.format(
        "%5s %3d %5s %3d %10s %4s %5s %10s %1s",
        "(From: ",
        this.getFromFloor(),
        " To: ",
        this.getToFloor()," Direction: ",
        this.getDirection(),
        " Type: ",this.getType(),
        ")");
  }
}
