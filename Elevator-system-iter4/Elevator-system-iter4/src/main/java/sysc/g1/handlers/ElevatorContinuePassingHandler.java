package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.CommandInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.LampState;
import sysc.g1.states.MotorState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;

import java.time.LocalTime;

public class ElevatorContinuePassingHandler extends Handler {

  @Override
  public void handleOnFloor(Event event, Floor floor) { }

  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) { }


  /**
   * handle scheduler's CommandInfo with type: ELEVATOR_CONTINUE_PASSING
   * <p>
   * increase/decrease elevator's level by 1
   * set motor state to CONSTANT_SPEED
   * make thread sleep
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
    if (info.getElevator() != elevator.getElevatorIndex()) {
      return;
    } else if (elevator.getCurrFloor() != info.getLevel()
            || elevator.getDoor().getState() == DoorState.OPEN
            || elevator.getMotor().getMotorState()==MotorState.IDLING
            || elevator.getMotor().getMotorState()==MotorState.DECELLERATING) {
      return;
    } else if (elevator.getCurrDirection() == Direction.UP && elevator.getCurrFloor() == elevator.getNumberOfFloors()) {
      return;
    } else if (elevator.getCurrDirection() == Direction.DOWN && elevator.getCurrFloor() == 1) {
      return;
    }

    //calculate next floor
    int nextFloor = elevator.getCurrFloor();
    nextFloor += elevator.getCurrDirection() == Direction.UP ? 1 : -1;

    elevator.setCurrFloor(nextFloor);
    elevator.getMotor().setMotorState(MotorState.CONSTANT_SPEED);

    //create event to send back
    LocalTime time = event.getTimestamp().plus(TimeUtil.CONTINUE_ONE_FLOOR);
    Event continueMoving = elevator.createElevatorEvent(time, EventType.ELEVATOR_ARRIVE);

    try {
      Thread.sleep(TimeUtil.CONTINUE_ONE_FLOOR.toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    elevator.display(continueMoving.getTimestamp(), String.format("Passing level %d", elevator.getCurrFloor()));
    elevator.notifyScheduler(continueMoving);
  }
}
