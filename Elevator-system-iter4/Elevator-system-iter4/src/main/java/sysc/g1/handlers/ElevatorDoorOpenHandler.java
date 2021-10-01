package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.entity.EntityType;
import sysc.g1.events.Event;
import sysc.g1.events.EventType;

import sysc.g1.events.*;

import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.FloorState;
import sysc.g1.utils.Direction;
import sysc.g1.utils.TimeUtil;


import java.time.LocalTime;

public class ElevatorDoorOpenHandler extends Handler {

  /**
   * handle loading when elevator door is open
   * sleep to simulate real-time loading time
   * send back LOADING_FINISH when done
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

    int elevatorId = info.getElevatorId();
    // Send loading finish event
    FloorResponseInfo floorInfo = new FloorResponseInfo(EventType.FLOOR_LOADING_FINISH, info.getDirection(),
            floor.getLevel(), elevatorId);

    LocalTime timestamp = event.getTimestamp().plus(TimeUtil.FLOOR_LOADING_DURATION);
    Event toBeSent = new Event(timestamp, EntityType.FLOOR, floorInfo, floor.getId(), SchedulerSubsystem.TARGET_ID);

    floor.display(event.getTimestamp(), "Start loading passenger...");

    // sleep to simulate real-time loading time
    try {
      Thread.sleep(TimeUtil.FLOOR_LOADING_DURATION.toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    floor.display(timestamp, "Finish loading passenger...");
    floor.removeState(FloorState.LOADING);
    floor.notifyScheduler(toBeSent);
  }


  /**
   * forward the event to Floor
   */
  @Override
  public void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem) {
    ElevatorInfo info;

    try {
      info = EventParser.parseInfo(ElevatorInfo.class, event.getInfo());
    } catch (ClassCastException e) {
      return;
    }

    String target = Floor.getTargetId(info.getCurrentFloor());
    Event toFloor = new Event(event.getTimestamp(), EntityType.SCHEDULER, info, schedulerSubsystem.getId(), target);
    schedulerSubsystem.notifyFloor(toFloor);
    schedulerSubsystem.display(event.getTimestamp(), String.format("Send 'DOOR OPEN' status to %s", target));
  }


  @Override
  public void handleOnElevator(Event event, Elevator elevator) {
  }
}
