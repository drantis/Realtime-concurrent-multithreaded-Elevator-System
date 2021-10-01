package sysc.g1.events;

import sysc.g1.utils.Direction;

public class ElevatorInfo extends EventInfo {

  private int currentFloor;
  private Direction direction;
  private int elevatorId;

  /**
   * Class constructor
   *
   * @param currentFloor location of the elevator
   * @param direction the direction the elevator is travelling in i.e. up or down
   */
  public ElevatorInfo(int id, EventType type, int currentFloor, Direction direction) {
    this.currentFloor = currentFloor;
    this.direction = direction;
    this.type = type;
    this.elevatorId = id;
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public Direction getDirection() {
    return direction;
  }

  public int getElevatorId() {
    return this.elevatorId;
  }

  @Override
  public String toString() {
    return String.format(
        "elevator %d - %s - floor %d - direction %s", elevatorId, type, currentFloor, direction);
  }
}
