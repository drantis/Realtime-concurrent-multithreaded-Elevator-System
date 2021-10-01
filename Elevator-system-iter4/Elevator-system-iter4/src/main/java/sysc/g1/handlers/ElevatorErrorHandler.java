package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.ElevatorErrorInfo;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.exceptions.FloorMismatchedStateException;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.FloorState;

public class ElevatorErrorHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
    ElevatorErrorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorErrorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }
    if (info.getErrorType().equals("door-stuck")) {
      if (floor.getArchiveStates().size() != 1) {
        System.out.println("Door stuck on a floor with no archive state??");
        return;
      }
      System.out.println(floor.getArchiveStates());
      if (floor.getArchiveStates().contains(FloorState.PENDING_DOWN)) {
        try {
          Event pressEvent = floor.pressDown(floor.getCurrentClockTime());
          floor.notifyScheduler(pressEvent);
        } catch (FloorMismatchedStateException e) {
          e.printStackTrace();
        }
      }
      if (floor.getArchiveStates().contains(FloorState.PENDING_UP)) {
        try {
          Event pressEvent = floor.pressUp(floor.getCurrentClockTime());
          floor.notifyScheduler(pressEvent);
        } catch (FloorMismatchedStateException e) {
          System.out.println("Here");
          e.printStackTrace();
        }
      }

      floor.cleanArchiveStates();
    }
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    ElevatorErrorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorErrorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    if (info.getErrorType().equals("door-stuck")) {
      schedulerSubsystem.getControllerBoard().disableElevator(info.getElevatorId());
      schedulerSubsystem.display(schedulerSubsystem.getCurrentClockTime(), "ERROR: Elevator" + info.getElevatorId() + "is stuck! Disabling...");
      int floor = schedulerSubsystem.getControllerBoard().getElevatorLocation(info.getElevatorId());
      Event floorEvent = new Event(
          schedulerSubsystem.getCurrentClockTime(),
          schedulerSubsystem.getEntityType(),
          info,
          schedulerSubsystem.getId(),
          Floor.getTargetId(floor));
      schedulerSubsystem.notifyFloor(floorEvent);
    }
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
    ElevatorErrorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorErrorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    if (info.getErrorType().equals("door-stuck")) {
      elevator.applyDoorStuckError(info.getDuration());
    }
  }
}
