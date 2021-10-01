package sysc.g1.floor;

import sysc.g1.utils.Direction;

public class DirectionLamp {
    private Direction direction;
    private int activeElevatorFloor;
    private int elevatorIndex;

    public DirectionLamp(int elevatorIndex) {
        this.direction = Direction.NONE;
        this.activeElevatorFloor = 0;
        this.elevatorIndex = elevatorIndex;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getActiveElevatorFloor() {
        return activeElevatorFloor;
    }

    public int getElevatorIndex() {
        return this.elevatorIndex;
    }

    public void setActiveElevatorFloor(int activeElevatorFloor) {
        this.activeElevatorFloor = activeElevatorFloor;
    }

    public String display(){
      return String.format(
          "Direction Lamp: elevator %d is at floor %d - Going %s",
          elevatorIndex,
          activeElevatorFloor,
          direction);
    }
}
