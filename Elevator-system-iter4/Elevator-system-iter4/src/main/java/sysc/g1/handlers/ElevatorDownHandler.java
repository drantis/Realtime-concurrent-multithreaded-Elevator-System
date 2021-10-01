package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.ElevatorState;
import sysc.g1.states.LampState;
import sysc.g1.states.MotorState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;

import java.time.LocalTime;

public class ElevatorDownHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) { }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    // Scheduler should not receive this
  }

  /**
   * handle scheduler ELEVATOR_DOWN
   *
   * turn off lamp at the current level
   * move down one floor
   * send back ELEVATOR_ARRIVE to scheduler
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

    // handle mistakes
    if (info.getElevator() != elevator.getElevatorIndex() ||
        elevator.getCurrFloor() != info.getLevel() ||
        elevator.getDoor().getState()== DoorState.OPEN ||
        elevator.getCurrFloor() == 1) {
      elevator.display(event.getTimestamp(), "Warning: Cannot go down");
      return;
    }

    //set lamp OFF (in case)
    elevator.setCurrFloor(elevator.getCurrFloor() - 1);
    elevator.getMotor().setMotorState(MotorState.ACCELERATING);
    elevator.setCurrDirection(Direction.DOWN);

    //create event to send back
    LocalTime time = event.getTimestamp().plus(TimeUtil.MOVE_ONE_FLOOR_DURATION);
    Event goDown = elevator.createElevatorEvent(time, EventType.ELEVATOR_ARRIVE);

    try{
      Thread.sleep(TimeUtil.MOVE_ONE_FLOOR_DURATION.toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    elevator.clearState(ElevatorState.PENDING);
    elevator.setState(ElevatorState.MOVE_DOWN);
    elevator.display(
        goDown.getTimestamp(), String.format("Moving down to level %d", elevator.getCurrFloor()));
    elevator.notifyScheduler(goDown);
  }
}
