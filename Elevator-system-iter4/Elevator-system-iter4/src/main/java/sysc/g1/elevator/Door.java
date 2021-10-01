package sysc.g1.elevator;

import sysc.g1.states.DoorState;

public class Door {
  private DoorState state;
  public Door() {
    this.state = DoorState.CLOSED;
  }

  public DoorState getState() {
    return state;
  }

  public void setState(DoorState state) {
    this.state = state;
  }

//  public Event open(LocalTime time, int currFloor, Direction currDirection){
//    EventInfo doorOpen = new ElevatorInfo(EventType.ELEVATOR_DOOR_OPEN, currFloor, currDirection);
//    return new Event(time, EntityType.ELEVATOR, doorOpen, "elevator-door-open");
//  }
//
//  public Event close(LocalTime time, int currFloor, Direction currDirection){
//    EventInfo doorClose = new ElevatorInfo(EventType.ELEVATOR_DOOR_CLOSE, currFloor, currDirection);
//    return new Event(time, EntityType.ELEVATOR, doorClose, "elevator-door-close");
//  }
}
