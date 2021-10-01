package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;

/**
 * Handler when we want to start the elevator subsystem
 */
public class ElevatorActivateHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
  }


  /**
   * Activate the elevator if it is not activated
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

    if (info.getElevator() != elevator.getElevatorIndex()) {
      return;
    }

    elevator.activate(event.getTimestamp());
  }
}
