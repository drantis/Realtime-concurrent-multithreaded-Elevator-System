package sysc.g1.handlers;

import java.time.Duration;
import java.time.LocalTime;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorErrorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.events.FloorResponseInfo;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.ElevatorState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;

public class FloorLoadingFinishHandler extends Handler {

  /**
   * Handle elevator door open event from scheduler:
   * - Send {@code FLOOR_LOADING_FINISH} to the scheduler
   *
   * @param event the incoming event
   * @param floor the floor object to handle this event
   */
  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    FloorResponseInfo info;

    //Parse in event info
    try {
      info = EventParser.parseInfo(FloorResponseInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    //Scheduler receives LOADING_FINISH status from floor to elevator
    String targetId = Elevator.getTargetId(info.getWorkingElevator());
    Event toBeSend = new Event(event.getTimestamp(), EntityType.SCHEDULER, info, schedulerSubsystem.getId(), targetId);
    schedulerSubsystem.notifyElevator(toBeSend);
    schedulerSubsystem.display(toBeSend.getTimestamp(), "Sending 'LOADING STATUS' from scheduler to elevator ");
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
    FloorResponseInfo info;

    //Parse event info
    try {
      info = EventParser.parseInfo(FloorResponseInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    if (elevator.getCurrFloor() != info.getFloor()) {
      return;
    }

    boolean stuck = false;
    Duration stuckDuration = Duration.ofSeconds(0);
    if (elevator.getDoorWillStuck() > 0) {
      stuckDuration = Duration.ofSeconds(elevator.getDoorWillStuck());
      ElevatorErrorInfo errorInfo = new ElevatorErrorInfo(
          elevator.getElevatorIndex(), "door-stuck", elevator.getDoorWillStuck());
      Event errorEvent = new Event(
          elevator.getCurrentClockTime(),
          elevator.getEntityType(),
          errorInfo,
          elevator.getId(),
          "scheduler");
      elevator.notifyScheduler(errorEvent);
      elevator.display(elevator.getCurrentClockTime(), "ERROR: Elevator door is stuck");
      stuck = true;
      try {
        Thread.sleep(stuckDuration.toMillis());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      elevator.fixDoorStuck();
    }

    //Closing door
    elevator.getDoor().setState(DoorState.CLOSED);
    LocalTime timestamp = elevator.getCurrentClockTime();
    Event toBeSend = elevator.createElevatorEvent(timestamp, EventType.ELEVATOR_PENDING);
    elevator.display(toBeSend.getTimestamp(), "Elevator closes door");

    try {
      Thread.sleep(TimeUtil.DOOR_CLOSE_DURATION.toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (!stuck) {
      Event doorClose =
          elevator.createElevatorEvent(
              elevator.getCurrentClockTime(), EventType.ELEVATOR_DOOR_CLOSE);
      elevator.notifyScheduler(doorClose);
    }


    elevator.clearState(ElevatorState.LOADING);
    elevator.setState(ElevatorState.PENDING);
    elevator.executeActorRequests(timestamp, elevator.getCurrFloor()).forEach(elevator::notifyScheduler);

    elevator.notifyScheduler(toBeSend);
  }
}
