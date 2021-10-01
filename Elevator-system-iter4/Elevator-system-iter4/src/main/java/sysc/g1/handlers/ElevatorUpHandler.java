package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.CommandInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.ElevatorState;
import sysc.g1.states.LampState;
import sysc.g1.states.MotorState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;

import java.time.LocalTime;

public class ElevatorUpHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) {
  }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
  }

  /**
   * handle scheduler's CommandInfo with type ELEVATOR_UP
   *
   * turn off lamp at current level
   * move up one floor
   * send back
   *
   * @param event the incoming event
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
    if (info.getElevator() != elevator.getElevatorIndex() ||
        elevator.getCurrFloor() != info.getLevel() ||
        elevator.getDoor().getState()== DoorState.OPEN ||
         elevator.getCurrFloor() == elevator.getNumberOfFloors()) {
      return;
    }

    //set lamp OFF (in case)
    elevator.getLamps()[elevator.getCurrFloor()].setState(LampState.OFF);
    elevator.setCurrFloor(elevator.getCurrFloor() + 1);
    elevator.getMotor().setMotorState(MotorState.ACCELERATING);
    elevator.setCurrDirection(Direction.UP);

    try {
			Thread.sleep(TimeUtil.MOVE_ONE_FLOOR_DURATION.getSeconds());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    //create event to send back
    LocalTime time = event.getTimestamp().plus(TimeUtil.MOVE_ONE_FLOOR_DURATION);
    Event goUp = elevator.createElevatorEvent(time, EventType.ELEVATOR_ARRIVE);
    elevator.display(goUp.getTimestamp(), String.format("Accelerating up to level %d", elevator.getCurrFloor()));

    try{
      Thread.sleep(TimeUtil.MOVE_ONE_FLOOR_DURATION.toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    elevator.clearState(ElevatorState.PENDING);
    elevator.setState(ElevatorState.MOVE_UP);

    elevator.notifyScheduler(goUp);
  }
}
