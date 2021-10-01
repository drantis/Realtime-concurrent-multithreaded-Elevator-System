package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.Event;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;

public class NullHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {

  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {

  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {

  }
}
