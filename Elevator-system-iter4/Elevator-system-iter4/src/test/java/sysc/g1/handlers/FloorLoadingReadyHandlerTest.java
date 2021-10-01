package sysc.g1.handlers;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;
import static sysc.g1.network.SubsystemPorts.FLOOR_IP;
import static sysc.g1.network.SubsystemPorts.FLOOR_PORT;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_IP;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_PORT;

import java.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sysc.g1.actors.ActorSimulator;
import sysc.g1.elevator.Elevator;
import sysc.g1.elevator.ElevatorSubsystem;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventType;
import sysc.g1.events.FloorResponseInfo;
import sysc.g1.floor.DirectionLamp;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.LampState;
import sysc.g1.utils.Direction;

public class FloorLoadingReadyHandlerTest {

  private static Floor floor;
  private static FloorSubsystem floorSubsystem;
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Elevator elevator;
  private static FloorLoadingReadyHandler handler;

  @BeforeClass
  public static void setUp() {
    handler = new FloorLoadingReadyHandler();
    // number of Floor or Elevator
    int numberOfFloor = 10;
    int numberOfElevator = 1;
    floorSubsystem = new FloorSubsystem(
        numberOfFloor,
        numberOfElevator,
        FLOOR_IP,
        FLOOR_PORT);
    elevatorSubsystem = new ElevatorSubsystem(
        numberOfFloor,
        numberOfElevator,
        ELEVATOR_IP,
        ELEVATOR_PORT);
    schedulerSubsystem = new SchedulerSubsystem(
        numberOfFloor,
        numberOfElevator,
        SCHEDULER_IP,
        SCHEDULER_PORT);
    floor = floorSubsystem.getFloor(4);
    elevator = elevatorSubsystem.getElevator(1);
  }

  @Test
  public void handleOnScheduler_succeeded() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    FloorResponseInfo testInfo =
        new FloorResponseInfo(EventType.FLOOR_LOADING_READY, Direction.UP, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.FLOOR, testInfo, "scheduler", floor.getId());

    // Fire the event
    handler.handleOnScheduler(testEvent, schedulerSubsystem);

    // Verify that it is good
    assertEquals(Direction.UP, Direction.UP);
  }

  @Test
  public void handleOnElevator_DoorOpened() {
    LocalTime now = LocalTime.of(3, 0);
    FloorResponseInfo testInfo =
        new FloorResponseInfo(EventType.FLOOR_LOADING_READY, Direction.NONE, 9, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    elevator.setCurrFloor(9);
    assertEquals(DoorState.CLOSED, elevator.getDoor().getState());

    handler.handleOnElevator(testEvent, elevator);

    assertEquals(DoorState.OPEN, elevator.getDoor().getState());
  }

  @Test
  public void handleOnElevator_LampOff() {
    LocalTime now = LocalTime.of(3, 0);
    FloorResponseInfo testInfo =
        new FloorResponseInfo(EventType.FLOOR_LOADING_READY, Direction.NONE, 9, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    elevator.setCurrFloor(9);

    assertEquals(elevator.getLamps()[elevator.getCurrFloor()].getState(), LampState.OFF);

    handler.handleOnElevator(testEvent, elevator);

    assertEquals(elevator.getLamps()[elevator.getCurrFloor()].getState(), LampState.OFF);
  }

  @Test
  public void handleOnElevator_fail() {
    LocalTime now = LocalTime.of(3, 0);
    FloorResponseInfo testInfo =
        new FloorResponseInfo(EventType.FLOOR_LOADING_READY, Direction.NONE, 9, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    elevator.setCurrFloor(3);

    handler.handleOnElevator(testEvent, elevator);

    assertEquals(DoorState.CLOSED, elevator.getDoor().getState());
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
    floorSubsystem.close();
  }
}
