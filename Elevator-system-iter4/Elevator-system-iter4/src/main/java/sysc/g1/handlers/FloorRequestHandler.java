package sysc.g1.handlers;

import java.time.LocalTime;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.LampState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;

public class FloorRequestHandler extends Handler {
  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  /**
   * Handle button up and down request from the floor:
   * - Store the request in the control board
   * - Try to activate the elevator
   *
   * @param event              the incoming event
   * @param schedulerSubsystem the schedule subsystem which will handle this event
   */
  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    FloorRequestInfo info;
    try {
      info = EventParser.parseInfo(FloorRequestInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    // Store request to the corresponding floor
    schedulerSubsystem.getControllerBoard().setFloorRequests(info.getDirection(), info.getFrom(), true);

    schedulerSubsystem.display(event.getTimestamp(), String.format("Received request to go %s from floor %d.",
            info.getDirection(), info.getFrom()));

    int bestElevatorId = schedulerSubsystem.getControllerBoard().findBestElevator(info.getFrom(), info.getDirection());

//    Direction reqDirection =
//            info.getFrom() > schedulerSubsystem.getControllerBoard().getElevatorLocation(bestElevatorId) ?
//                    Direction.UP : Direction.DOWN;
    
    schedulerSubsystem.getControllerBoard().setElevatorRequest(bestElevatorId, info.getFrom(), info.getDirection());


    CommandInfo sendInfo = new CommandInfo(EventType.ELEVATOR_ACTIVATE, info.getFrom(), bestElevatorId);

    String targetId = Elevator.getTargetId(bestElevatorId);
    Event sendEvent = new Event(event.getTimestamp(), schedulerSubsystem.getEntityType(), sendInfo,
            schedulerSubsystem.getId(), targetId);
    schedulerSubsystem.notifyElevator(sendEvent);
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
  }
}
