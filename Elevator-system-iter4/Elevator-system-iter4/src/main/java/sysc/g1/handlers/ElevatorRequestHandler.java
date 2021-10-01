package sysc.g1.handlers;

import java.time.LocalTime;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.ElevatorRequestInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.utils.Direction;

public class ElevatorRequestHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  /**
   * Handles button press from inside the elevator:
   * - Turn button press light on if possible
   *
   * @param event              the incoming event
   * @param schedulerSubsystem the schedule subsystem which will handle this event
   */
  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    ElevatorRequestInfo info;

    try {
      info = EventParser.parseInfo(ElevatorRequestInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    if (info.getToFloor() == info.getFromFloor()) {
      return;
    }

    Direction reqDirection = info.getToFloor() > info.getFromFloor() ? Direction.UP : Direction.DOWN;

    int eId = info.getElevatorId();

    // Setting the location of the stopped elevator to the current floor
    schedulerSubsystem.getControllerBoard().setElevatorLocation(eId, info.getFromFloor());

    // setting to elevator with the elevatorId in Scheduler board
    schedulerSubsystem.getControllerBoard().setElevatorRequest(eId, info.getToFloor(), reqDirection);

    schedulerSubsystem.display(event.getTimestamp(), String.format("Received elevator button press to go to %d",
            info.getToFloor()));

    if (schedulerSubsystem.getControllerBoard().getElevatorDirection(eId) == Direction.NONE) {
      ElevatorInfo sendInfo = new ElevatorInfo(eId, EventType.ELEVATOR_ACTIVATE, info.getFromFloor(), Direction.NONE);
      String targetId = Elevator.getTargetId(eId);
      Event sendEvent = new Event(event.getTimestamp(), schedulerSubsystem.getEntityType(), sendInfo,
              schedulerSubsystem.getId(), targetId);
      schedulerSubsystem.notifyElevator(sendEvent);
    }
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
  }
}
