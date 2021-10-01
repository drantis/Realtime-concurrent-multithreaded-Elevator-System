package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.ElevatorRequestInfo;
import sysc.g1.events.Event;
import sysc.g1.exceptions.FloorMismatchedStateException;
import sysc.g1.exceptions.InvalidEventTypeException;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.utils.Direction;
import sysc.g1.utils.EventParser;

public class ButtonPressHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
    try {
      ElevatorRequestInfo info = EventParser.parse(ElevatorRequestInfo.class, event.getInfo());
      System.out.println(
          "Moving from Floor: "
              + info.getFromFloor()
              + " at time: "
              + event.getTimestamp()
              + " to Floor: "
              + info.getToFloor());

      if (info.getDirection() == Direction.UP) {
        floor.notifyScheduler(floor.pressUp(event.getTimestamp()));
      } else if (info.getDirection() == Direction.DOWN) {
        floor.notifyScheduler(floor.pressDown(event.getTimestamp()));
      }
    } catch (FloorMismatchedStateException | InvalidEventTypeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {}

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
    try {
      ElevatorRequestInfo info = EventParser.parse(ElevatorRequestInfo.class, event.getInfo());
      System.out.println(
          "Moving from Floor: "
              + info.getFromFloor()
              + " at time: "
              + event.getTimestamp()
              + " to Floor: "
              + info.getToFloor());
      elevator.setActorRequests(info.getFromFloor(), info.getToFloor());

    } catch (InvalidEventTypeException e) {
      e.printStackTrace();
    }
  }
}
