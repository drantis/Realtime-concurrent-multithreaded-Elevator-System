package sysc.g1.floor;

import sysc.g1.events.FloorRequestInfo;
import sysc.g1.utils.Direction;

/**
 * The floor button class that can simulate the press action
 */
public class FloorButton {
  private Direction direction;
  private int floor;

  /**
   * The constructor of the FloorButton class
   *
   * @param direction the direction to move
   * @param floor     the floor of this button
   */
  public FloorButton(Direction direction, int floor) {
    this.direction = direction;
    this.floor = floor;
  }

  public Direction getDirection() {
    return direction;
  }

  /**
   * Press this floor button
   *
   * @return the information of the press event, including direction and this floor
   */
  public FloorRequestInfo press() {
    return new FloorRequestInfo(direction, floor);
  }
}
