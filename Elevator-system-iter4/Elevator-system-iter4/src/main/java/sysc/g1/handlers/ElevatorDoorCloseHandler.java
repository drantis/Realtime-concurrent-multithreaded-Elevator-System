package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;

public class ElevatorDoorCloseHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
    ElevatorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    if (info.getCurrentFloor() != floor.getLevel()) {
      System.out.println("Door close sent to incorrect floor");
      return;
    }

    floor.cleanArchiveStates();
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    ElevatorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    int floorId = info.getCurrentFloor();
    Event transferEvent = new Event(
        schedulerSubsystem.getCurrentClockTime(),
        schedulerSubsystem.getEntityType(),
        info,
        schedulerSubsystem.getId(),
        Floor.getTargetId(floorId));
    schedulerSubsystem.notifyFloor(transferEvent);
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {

  }
}
