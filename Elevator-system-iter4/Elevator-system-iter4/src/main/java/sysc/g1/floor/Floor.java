package sysc.g1.floor;

import java.util.ArrayList;
import java.util.List;
import sysc.g1.entity.Entity;
import sysc.g1.entity.EntityType;
import sysc.g1.events.Event;
import sysc.g1.events.EventType;
import sysc.g1.events.FloorRequestInfo;
import sysc.g1.exceptions.FloorMismatchedStateException;
import sysc.g1.states.FloorState;
import sysc.g1.utils.Direction;
import sysc.g1.states.LampState;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Floor extends Entity {
  private int level;
  private FloorButton upButton, downButton;
  private List<DirectionLamp> directionLamps; // elevator is going up or down?
  private FloorLamp upLamp, downLamp;
  private FloorSubsystem floorSubsystem;
  private Set<FloorState> activeStates;
  private int numberOfElevators;
  private Set<FloorState> archiveStates;

  public static final String TARGET_ID = "scheduler";

  public Floor(int level, FloorSubsystem system, int numberOfElevators) {
    super(EntityType.FLOOR, Floor.getTargetId(level));
    this.floorSubsystem = system;
    this.level = level;
    this.activeStates = Collections.synchronizedSet(new HashSet<>());
    this.archiveStates = Collections.synchronizedSet(new HashSet<>());
    this.upLamp = new FloorLamp(level, Direction.UP);
    this.downLamp = new FloorLamp(level, Direction.DOWN);
    this.upButton = new FloorButton(Direction.UP, level);
    this.downButton = new FloorButton(Direction.DOWN, level);
    this.numberOfElevators = numberOfElevators;
    this.directionLamps = new ArrayList<>(numberOfElevators);
    for (int i=1; i<=numberOfElevators; i++) {
      directionLamps.add(new DirectionLamp(i));
    }
  }

  public Set<FloorState> getActiveStates() {
    return activeStates;
  }

  public static String getTargetId(int level) {
    return "floor_" + level;
  }

  public static int getLevelFromTarget(String targetId) {
    String[] parts = targetId.split("_");
    if (!parts[0].equals("floor")) {
      return 0;
    }
    return Integer.parseInt(parts[1]);
  }

  public int getLevel() {
    return level;
  }

  public FloorLamp getLamp(Direction direction) {
    return direction == Direction.UP ? upLamp : downLamp;
  }

  public DirectionLamp getDirectionLamp(int elevatorIndex) {
    return directionLamps.get(elevatorIndex - 1);
  }

  //////////////////////
  // RECEIVING EVENTS //
  //////////////////////
  @Override
  public void processEvent(Event event) {
    EventType eventType = event.getInfo().getType();
    boolean shouldHandle = true;

    // Elevator Door should not be opened when elevator is not in a loading state
    if (eventType == EventType.ELEVATOR_DOOR_OPEN && !activeStates.contains(FloorState.LOADING)) {
      shouldHandle = false;
    }

    // Elevator door should not be opened when the floor is pending up AND down
    else if (eventType == EventType.ELEVATOR_DOOR_OPEN
        && (activeStates.contains(FloorState.PENDING_UP)
            && activeStates.contains(FloorState.PENDING_DOWN))) {
      shouldHandle = false;
    }

    // Elevator arrive should not be handled when the floor is still in a loading state
    else if (eventType == EventType.ELEVATOR_ARRIVE && activeStates.contains(FloorState.LOADING)) {
      display(event.getTimestamp(), "Warning: elevator arrives when loading, abort!!!");
      shouldHandle = false;
    }

    else if (eventType == EventType.ELEVATOR_STOP &&  activeStates.contains(FloorState.LOADING)) {
      display(event.getTimestamp(), "Warning: elevator stop when loading, abort!!!");
    }

    else {
      shouldHandle = true;
    }

    if (shouldHandle) {
      event.getHandler().handleOnFloor(event, this);
    }
  }

  public void notifyScheduler(Event event) {
    display(event.getTimestamp(), String.format("Notify Scheduler: %s", event));
    floorSubsystem.floorToScheduler(event);
  }

  /////////////
  // ACTIONS //
  /////////////

  /** Simulate press up event */
  public Event pressUp(LocalTime timestamp) throws FloorMismatchedStateException {
//    if (activeStates.isEmpty()) {
//      throw FloorMismatchedStateException.create("Idling when press button", activeStates);
//    }
//    if (upLamp.getState() == LampState.ON || activeStates.contains(FloorState.LOADING)) {
//      System.out.println("NULLLLLLLLLLLLLLLLL");
//      return Event.getNullEvent();
//    }
    FloorRequestInfo info = upButton.press();

    upLamp.switchLamp(LampState.ON);
    activeStates.add(FloorState.PENDING_UP);

    return new Event(timestamp, entityType, info, id, TARGET_ID);
  }

  /** Simulate pressing down event */
  public Event pressDown(LocalTime timestamp) throws FloorMismatchedStateException {
//    if (activeStates.isEmpty()) {
//      throw FloorMismatchedStateException.create("Idling when press button", activeStates);
//    }
    if (downLamp.getState() == LampState.ON || activeStates.contains(FloorState.LOADING)) {
      return Event.getNullEvent();
    }
    FloorRequestInfo info = downButton.press();

    downLamp.switchLamp(LampState.ON);
    activeStates.add(FloorState.PENDING_DOWN);

    return new Event(timestamp, entityType, info, id, TARGET_ID);
  }

  public void addState(FloorState state) {
    this.activeStates.add(state);
  }

  public void addArchiveState(FloorState state) {
    this.archiveStates.add(state);
  }

  public Set<FloorState> getArchiveStates() {
    return archiveStates;
  }

  public void removeState(FloorState state) {
    this.activeStates.remove(state);
  }

  public void cleanArchiveStates() {
    this.archiveStates.clear();
  }
}
