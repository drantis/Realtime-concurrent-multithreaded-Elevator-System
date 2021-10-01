package sysc.g1.handlers;

import java.time.LocalTime;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.ElevatorState;
import sysc.g1.states.FloorState;
import sysc.g1.states.LampState;
import sysc.g1.states.MotorState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;

public class ElevatorStopHandler extends Handler {

  /**
   * Handle elevator stop event from scheduler:
   * - Turn floor light off if possible
   * - Send {@code FLOOR_LOADING_READY} to the scheduler
   *
   * @param event the incoming event
   * @param floor the floor object to handle this event
   */
  @Override
  public void handleOnFloor(Event event, Floor floor) {
    // Parse info
    ElevatorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }
    if (floor.getLevel() != info.getCurrentFloor()) {
      return;
    }

    // Switch lamp off
    floor.getLamp(info.getDirection()).switchLamp(LampState.OFF);

    //remove the floor pending state for the current direction
    if (info.getDirection() == Direction.UP) {
      floor.removeState(FloorState.PENDING_UP);
      floor.addArchiveState(FloorState.PENDING_UP);
    } else if (info.getDirection() == Direction.DOWN) {
      floor.removeState(FloorState.PENDING_DOWN);
      floor.addArchiveState(FloorState.PENDING_DOWN);
    }

    // Send ready event
    FloorResponseInfo floorInfo = new FloorResponseInfo(EventType.FLOOR_LOADING_READY, info.getDirection(),
            info.getCurrentFloor(), info.getElevatorId());

    LocalTime timestamp = event.getTimestamp().plus(TimeUtil.SWITCH_LAMP_DURATION);
    Event toBeSend = new Event(timestamp, EntityType.FLOOR, floorInfo, floor.getId(), Floor.TARGET_ID);

    floor.display(toBeSend.getTimestamp(), String.format("Turn %s light off!", info.getDirection()));
    // Change the floor state to loading, blocking arrive events
    floor.addState(FloorState.LOADING);
    floor.notifyScheduler(toBeSend);
  }

  /**
   * handles ELEVATOR_STOP from elevator and forwards it to Floor
   *
   * @param event              the incoming event
   * @param schedulerSubsystem the schedule subsystem which will handle this event
   */
  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    ElevatorInfo info;

    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    //Sending 'STOP' status from scheduler to floor
    String target = Floor.getTargetId(info.getCurrentFloor());
    Event toFloor = new Event(event.getTimestamp(), EntityType.SCHEDULER, info, schedulerSubsystem.getId(), target);
    schedulerSubsystem.notifyFloor(toFloor);

    schedulerSubsystem.display(event.getTimestamp(), String.format("Sending 'ELEVATOR STOP' from scheduler to %s",
            target));
  }

  /**
   * handles ELEVATOR_STOP event from Scheduler
   * elevator decelerates and send back ELEVATOR_STOP to Scheduler to confirm
   *
   * @param event    the incoming event
   * @param elevator the elevator thread which will handle this event
   */
  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
    CommandInfo info;
    try {
      info = EventParser.parseInfo(CommandInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    //handle mistakes
    if (elevator.getCurrFloor() != info.getLevel() || elevator.getDoor().getState() == DoorState.OPEN) {
      return;
    }

    //create event to send back
    LocalTime time = event.getTimestamp().plus(TimeUtil.MOVE_ONE_FLOOR_DURATION);
    Event stopEvent = elevator.createElevatorEvent(time, EventType.ELEVATOR_STOP);

    if (elevator.getCurrDirection() != Direction.NONE) {
      elevator.getMotor().setMotorState(MotorState.DECELLERATING);
      elevator.display(
          stopEvent.getTimestamp(),
          String.format("Elevator's decelerating to level %d", elevator.getCurrFloor()));

      try {
        Thread.sleep(TimeUtil.MOVE_ONE_FLOOR_DURATION.toMillis());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    elevator.clearState(ElevatorState.MOVE_DOWN);
    elevator.clearState(ElevatorState.MOVE_UP);
    elevator.setState(ElevatorState.LOADING);

    elevator.notifyScheduler(stopEvent);
  }
}
