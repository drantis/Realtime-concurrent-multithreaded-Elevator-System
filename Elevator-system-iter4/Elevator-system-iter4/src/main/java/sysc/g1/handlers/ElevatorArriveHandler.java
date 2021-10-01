package sysc.g1.handlers;

import java.time.LocalTime;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.CommandInfo;
import sysc.g1.events.EventInfo;
import sysc.g1.events.EventParser;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.ControllerBoard;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.utils.Direction;

public class ElevatorArriveHandler extends Handler {

  /**
   * Handle elevator arrive event from scheduler:
   *
   * <p>- Send {@code FLOOR_ELEVATOR_ARRIVAL_CONFIRM} to the scheduler
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

    int elevatorIndex = info.getElevatorId();

    // Switch floor lamp of elevator direction on
    floor.getDirectionLamp(elevatorIndex).setDirection(info.getDirection());
    floor.getDirectionLamp(elevatorIndex).setActiveElevatorFloor(info.getCurrentFloor());

//    floor.display(event.getTimestamp(), String.format(
//            "Direction lamp: %s %d",
//            floor.getDirectionLamp(elevatorIndex).getDirection(),
//            floor.getDirectionLamp(elevatorIndex).getActiveElevatorFloor()));
  }

  /**
   * The scheduler handles the event when the elevator arrives
   *
   * <p>- If there's no request on this floor then send a command to keep moving - If there's a
   * request on this floor then send a command to stop: - stop when there
   *
   * @param event              the incoming event
   * @param schedulerSubsystem the schedule subsystem which will handle this event
   */
  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    // Parse info
    ElevatorInfo info;
    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    ControllerBoard board = schedulerSubsystem.getControllerBoard();
    Direction dir = info.getDirection();
    int arrivingFloor = info.getCurrentFloor();
    int elevatorIndex = info.getElevatorId();

    EventInfo sendingInfo;

    // Update the state of this elevator on the controller board
    board.setElevatorLocation(elevatorIndex, arrivingFloor);
    board.setElevatorDirections(elevatorIndex, dir);

    boolean switchDirection =
        board.hasFloorRequest(board.getOppositeDirection(dir), arrivingFloor)
            && !board.hasFurtherRequest(elevatorIndex, arrivingFloor, dir);

    // Turn the request off and send event to stop the elevator
    if (board.hasFloorRequest(dir, arrivingFloor) ||
            (arrivingFloor == 1 || arrivingFloor == board.getTotalFloors()) ||
            board.hasElevatorRequest(elevatorIndex, arrivingFloor, dir) || switchDirection) {

      // Setting the the elevator request for the floor and elevator off
//      System.out.println("has floor request: " + board.hasFloorRequest(dir, arrivingFloor));
//      System.out.println("has elevator request: " + board.hasElevatorRequest(elevatorIndex, arrivingFloor,dir));
      board.clearRequest(arrivingFloor, dir);
      if (switchDirection) {
//        System.out.println("Switch direction");
        board.clearRequest(arrivingFloor, board.getOppositeDirection(dir));
      }
      if (arrivingFloor == 1 || arrivingFloor == board.getTotalFloors()) {
        board.clearRequest(arrivingFloor, Direction.UP);
        board.clearRequest(arrivingFloor, Direction.DOWN);
      }

      sendingInfo = new CommandInfo(EventType.ELEVATOR_STOP, arrivingFloor, elevatorIndex);
    } else {
      sendingInfo = new CommandInfo(EventType.ELEVATOR_CONTINUE_PASSING, arrivingFloor, elevatorIndex);
    }

    // Send command to the elevator
    Event toBeSent = new Event(
            event.getTimestamp(),
            EntityType.SCHEDULER,
            sendingInfo,
            schedulerSubsystem.getId(),
            Elevator.getTargetId(elevatorIndex));

    schedulerSubsystem.display(
            toBeSent.getTimestamp(),
            String.format("Sending command to elevator: %s", sendingInfo.getType()));
    schedulerSubsystem.notifyElevator(toBeSent);

    // sending ElevatorInfo to every floor
    for (int i = 1; i <= schedulerSubsystem.getNumberOfFloors(); i++) {
      String target = Floor.getTargetId(i);
      Event sendToFloor = new Event(
              event.getTimestamp(), EntityType.SCHEDULER, info, schedulerSubsystem.getId(), target);
      schedulerSubsystem.notifyFloor(sendToFloor);
    }
    schedulerSubsystem.display(event.getTimestamp(), "Sending elevator location to every floor");
  }

  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
  }

}
