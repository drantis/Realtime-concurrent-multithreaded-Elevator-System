package sysc.g1.handlers;

import java.time.LocalTime;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.Event;

import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.events.FloorResponseInfo;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.LampState;
import sysc.g1.utils.TimeUtil;

public class FloorLoadingReadyHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  /**
   * receives Floor-ready event and forwards it to Elevator
   */
  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {

    FloorResponseInfo info;
    try {
      info = EventParser.parseInfo(FloorResponseInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    // Send floor loading ready event to elevator
    String targetId = Elevator.getTargetId(info.getWorkingElevator());
    Event toBeSend = new Event(event.getTimestamp(), EntityType.SCHEDULER, info, schedulerSubsystem.getId(), targetId);
    schedulerSubsystem.display(toBeSend.getTimestamp(), String.format("Floor %d is ready to be loaded...",
            info.getFloor()));
    schedulerSubsystem.notifyElevator(toBeSend);
  }

  /**
   * receives floor-loading-ready and opens door
   */
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

    // Opening the door and turning off the door
    elevator.getDoor().setState(DoorState.OPEN);
    elevator.getLamps()[elevator.getCurrFloor()].setState(LampState.OFF);

    //Creating an event to send back to the scheduler
    LocalTime timestamp = event.getTimestamp().plus(TimeUtil.DOOR_OPEN_DURATION);
    Event toBeSend = elevator.createElevatorEvent(timestamp, EventType.ELEVATOR_DOOR_OPEN);

    elevator.display(toBeSend.getTimestamp(), "Elevator opens door");
    try {
      Thread.sleep(TimeUtil.DOOR_OPEN_DURATION.toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    elevator.notifyScheduler(toBeSend);
  }
}
