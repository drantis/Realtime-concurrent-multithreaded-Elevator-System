package sysc.g1.handlers;

import java.util.Arrays;
import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.ControllerBoard;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.utils.Direction;

public class ElevatorPendingHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {

  }


  /**
   * processes the ElevatorInfo with ControllerBoard
   * to calculate the next EventType to send to Elevator
   *
   * @param controllerBoard scheduler's controllerBoard
   * @param info            ElevatorInfo received from elevator
   * @return EventType to be sent to elevator
   */
  public EventType getNextEventType(ControllerBoard controllerBoard, ElevatorInfo info) {
    // Next move: DOWN, UP, DEACTIVATE
    // calculate the next direction for elevator
    int pendingElevator = info.getElevatorId();
    int pendingLocation = controllerBoard.getElevatorLocation(pendingElevator);

    // If the elevator is idling and receive request on the same floor
    if (controllerBoard.getElevatorDirection(pendingElevator) == Direction.NONE &&
       (controllerBoard.getDownFloorRequests()[pendingLocation-1] ||
        controllerBoard.getUpFloorRequests()[pendingLocation-1])) {
      controllerBoard.clearRequest(pendingLocation, Direction.UP);
      controllerBoard.clearRequest(pendingLocation, Direction.DOWN);
      return EventType.ELEVATOR_STOP;
    }


    Direction dir = controllerBoard.getNextDirection(pendingElevator);

    if (dir == Direction.DOWN) {
      controllerBoard.setElevatorDirections(pendingElevator, dir);
      return EventType.ELEVATOR_DOWN;
    } else if (dir == Direction.UP) {
      controllerBoard.setElevatorDirections(pendingElevator, dir);
      return EventType.ELEVATOR_UP;
    }
    controllerBoard.setElevatorDirections(pendingElevator, Direction.NONE);
    return EventType.ELEVATOR_DEACTIVATE;
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    ElevatorInfo info;

    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    schedulerSubsystem.getControllerBoard().enableElevator(info.getElevatorId());
    EventType nextMove = getNextEventType(schedulerSubsystem.getControllerBoard(), info);
    CommandInfo newInfo = new CommandInfo(nextMove, info.getCurrentFloor(), info.getElevatorId());
    Event toBeSent = new Event(
            event.getTimestamp(),
            EntityType.SCHEDULER,
            newInfo,
            schedulerSubsystem.getId(),
            event.getSourceId());
    schedulerSubsystem.display(event.getTimestamp(), String.format("Sending direction to Elevator: %s", nextMove));
    schedulerSubsystem.notifyElevator(toBeSent);
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {

  }
}
