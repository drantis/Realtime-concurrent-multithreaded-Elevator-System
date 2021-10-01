package sysc.g1.elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sysc.g1.entity.Entity;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventInfo;
import sysc.g1.events.EventType;
import sysc.g1.states.ElevatorState;
import sysc.g1.states.MotorState;
import sysc.g1.utils.Direction;
import sysc.g1.states.DoorState;
import sysc.g1.utils.EventQueue;
import sysc.g1.states.LampState;

import java.time.LocalTime;

public class Elevator extends Entity {
  private int currFloor, numberOfFloors;
  private Direction currDirection;
  private ElevatorLamp[] lamps;
  private ElevatorButton[] buttons;
  private Motor motor;
  private Door door;
  private ElevatorSubsystem subsystem;
  private EventQueue eventQueue;
  private Set<ElevatorState> states;
  private int elevatorIndex;
  private int doorWillStuck;
  private boolean doorStuckError;

  public Elevator(int index, ElevatorSubsystem subsystem, int numberOfFloors) {
    super(EntityType.ELEVATOR, Elevator.getTargetId(index));
    this.currFloor = 1;
    this.currDirection = Direction.NONE;
    this.elevatorIndex = index;
    this.subsystem = subsystem;
    this.numberOfFloors = numberOfFloors;
    this.door = new Door();
    this.motor = new Motor();
    // IMPORTANT: states must be a synchronized collection
    this.states = Collections.synchronizedSet(new HashSet<>());
    this.doorWillStuck = 0;
    this.doorStuckError = false;

    this.eventQueue = new EventQueue();

    this.lamps = new ElevatorLamp[numberOfFloors + 1];
    this.buttons = new ElevatorButton[numberOfFloors + 1];

    for (int i = 0; i <= numberOfFloors; i++) {
      lamps[i] = new ElevatorLamp(i);
      buttons[i] = new ElevatorButton(elevatorIndex, id, i);
    }
  }

  public void setCurLocation(int floor) {
    this.currFloor = floor;
  }

  public static String getTargetId(int eleIndex){
    return "elevator_" + eleIndex;
  }

  public static int getElevatorFromTarget(String targetId) {
    String[] parts = targetId.split("_");
    if (!parts[0].equals("elevator")) {
      return 0;
    }
    return Integer.parseInt(parts[1]);
  }

  public int getElevatorIndex() {
    return elevatorIndex;
  }

  public List<Event> executeActorRequests(LocalTime timestamp, int currFloor) {
    return subsystem.executeActorRequests(elevatorIndex, timestamp, currFloor);
  }

  public int getCurrFloor() {
    return currFloor;
  }

  public Direction getCurrDirection() {
    return currDirection;
  }

  public ElevatorLamp[] getLamps() {
    return lamps;
  }

  public ElevatorButton[] getButtons() {
    return buttons;
  }

  public Motor getMotor() {
    return motor;
  }

  public Door getDoor() {
    return door;
  }

  public Set<ElevatorState> getStates() {
    return states;
  }

  public void setState(ElevatorState state) {
    this.states.add(state);
  }

  public void clearState(ElevatorState state) {
    this.states.remove(state);
  }

  public void setCurrFloor(int currFloor) {
    this.currFloor = currFloor;
  }

  public void setCurrDirection(Direction currDirection) {
    this.currDirection = currDirection;
  }

  /** Load passengers */
  public void onDoorAction(DoorState doorState) {
    door.setState(doorState);
    this.lamps[this.currFloor].setState(LampState.OFF);
    motor.setMotorState(MotorState.IDLING);
  }

  public Event createElevatorEvent(LocalTime time, EventType type) {
    EventInfo info = new ElevatorInfo(elevatorIndex, type, currFloor, currDirection);
    return new Event(time, EntityType.ELEVATOR, info, id, "scheduler");
  }

  public void notifyScheduler(Event event) {
    display(event.getTimestamp(), String.format("Notify Scheduler: %s", event));
    subsystem.elevatorToScheduler(event);
  }

  public int getNumberOfFloors() {
    return numberOfFloors;
  }

  /** press a button in elevator */
  public Event buttonPressed(LocalTime time, int buttonNum) {
    Event buttonEvent = this.buttons[buttonNum].press(time, currFloor);
    this.lamps[buttonNum].setState(LampState.ON);
    return buttonEvent;
  }

  /** process the event received from the subsystem */
  public void processEvent(Event event) {
    EventType eventType = event.getInfo().getType();
    boolean shouldHandle = true;

    if (eventType == EventType.ELEVATOR_ACTIVATE && !states.isEmpty()) {
      display(event.getTimestamp(), "Warning: Activate when the elevator is not idle!!!");
      shouldHandle = false;
    }
    // If the elevator receives accelerating events, abort if:
    //  - It's not currently pending for the accelrating events
    //  - It's is moving down or up
    if ((eventType == EventType.ELEVATOR_DOWN || eventType == EventType.ELEVATOR_UP)
        && (!states.contains(ElevatorState.PENDING)
            || states.contains(ElevatorState.MOVE_DOWN)
            || states.contains(ElevatorState.MOVE_UP))) {
//      System.out.println(states);
      display(event.getTimestamp(), "Warning: Receiving start moving event while moving!!");
      shouldHandle = false;

    // If the elevator receive passing or stop events, abort if:
    //   - It's in the pending state
    //   - It's not moving up or moving down
    } else if ((eventType == EventType.ELEVATOR_CONTINUE_PASSING)
        && states.contains(ElevatorState.PENDING)) {
      display(
          event.getTimestamp(),
          "Warning: Receiving movement adjustment event moving event while pending!!");
      shouldHandle = false;
    } else if (eventType == EventType.ELEVATOR_CONTINUE_PASSING
        && !(states.contains(ElevatorState.MOVE_UP) || states.contains(ElevatorState.MOVE_DOWN))) {
      display(
          event.getTimestamp(),
          "Warning: Receiving movement adjustment event moving event while moving!!");
      shouldHandle = false;
    }

    //Do not allow the elevator to handle loading ready, loading finish or door open if it is not in a loading state
    else if (
    		(eventType == EventType.FLOOR_LOADING_READY || eventType == EventType.FLOOR_LOADING_FINISH)
    			&&
    		!states.contains(ElevatorState.LOADING)) {
    	display(event.getTimestamp(), "Warning: Trying to perform loading events before the elevator is in a loading state!!!");
  		shouldHandle = false;
  	}

    else if (eventType == EventType.ELEVATOR_DEACTIVATE && states.isEmpty()) {
      display(event.getTimestamp(), "Warning: Deactivate when the elevator is idle!!!");
      shouldHandle = false;
    }

    if (shouldHandle) {
      event.getHandler().handleOnElevator(event, this);
    }
  }

  public boolean isActive() {
    return !this.states.isEmpty();
  }

  /** Activate the elevator if it's not activated */
  public void activate(LocalTime timestamp) {
    if (isActive()) {
      return;
    }

    states.add(ElevatorState.PENDING);
    notifyScheduler(requestCommand(timestamp, Direction.NONE, currFloor));
  }

  public void applyDoorStuckError(int duration) {
    this.doorWillStuck = duration;
  }

  public int getDoorWillStuck() {
    return doorWillStuck;
  }

  public void fixDoorStuck() {
    this.doorWillStuck = -1;
  }

  /** Deactivate the elevator if is activated */
  public void deactivate(LocalTime timestamp) {
    states.clear();
  }

  public Event requestCommand(LocalTime timestamp, Direction lastDirection, int floor) {
    ElevatorInfo info = new ElevatorInfo(elevatorIndex, EventType.ELEVATOR_PENDING, floor, lastDirection);
    return new Event(timestamp, EntityType.ELEVATOR, info, id, "scheduler");
  }

  public void setActorRequests(int fromFloor, int toFloor) {
    subsystem.setActorRequests(fromFloor, toFloor);
  }
}
