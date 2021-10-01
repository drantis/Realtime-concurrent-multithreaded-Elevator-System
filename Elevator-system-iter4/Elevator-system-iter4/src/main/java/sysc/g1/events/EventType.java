package sysc.g1.events;

import sysc.g1.handlers.*;

public enum EventType {
  HAND_SHAKE(new NullHandler()),
  BUTTON_PRESS(new ButtonPressHandler()),

  ELEVATOR_ACTIVATE(new ElevatorActivateHandler()),
  ELEVATOR_DEACTIVATE(new ElevatorDeactivateHandler()),

  FLOOR_REQUEST(new FloorRequestHandler()), // Actor,

  ELEVATOR_ARRIVE(new ElevatorArriveHandler()), // Floor
  ELEVATOR_STOP(new ElevatorStopHandler()), // Floor
  ELEVATOR_DOOR_OPEN (new ElevatorDoorOpenHandler()), // Floor
  ELEVATOR_DOOR_CLOSE (new ElevatorDoorCloseHandler()), // Floor

  ELEVATOR_REQUEST(new ElevatorRequestHandler()), // Actor
  ELEVATOR_PENDING(new ElevatorPendingHandler()), // Actor
  FLOOR_LOADING_READY(new FloorLoadingReadyHandler()),
  FLOOR_LOADING_FINISH(new FloorLoadingFinishHandler()), // Elevator

  ELEVATOR_UP(new ElevatorUpHandler()), // Scheduler
  ELEVATOR_DOWN(new ElevatorDownHandler()), // Scheduler
  ELEVATOR_CONTINUE_PASSING(new ElevatorContinuePassingHandler()), // Scheduler

  FLOOR_LOADING_ERROR(new FloorLoadingErrorHandler()),
  ELEVATOR_ERROR(new ElevatorErrorHandler());

  private Handler handler;

  public Handler getHandler() {
    return handler;
  }

  EventType(Handler handler) {
    this.handler = handler;
  }
}
