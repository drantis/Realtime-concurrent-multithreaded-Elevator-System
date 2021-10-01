package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;

/**
 * Handler when we want to start the elevator subsystem
 */
public class ElevatorDeactivateHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
  }

  /**
   * Deactivate the elevator if it is activated
   *
   * @param event    the incoming event
   * @param elevator the elevator thread which will handle this event
   */
  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
    elevator.deactivate(event.getTimestamp());
  }
}
