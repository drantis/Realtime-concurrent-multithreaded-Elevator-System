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
import sysc.g1.floor.DirectionLamp;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.LampState;
import sysc.g1.utils.Direction;

public class ElevatorArriveHandlerTest {

  private static Floor floor;
  private static FloorSubsystem floorSubsystem;
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Elevator elevator;

  private static ElevatorArriveHandler handler;

  private static int elevatorIndex = 0;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorArriveHandler();
    //number of Floor or Elevator
    int numberOfFloor = 10;
    int numberOfElevator = 2;
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
    elevatorIndex = elevator.getElevatorIndex();
  }

  @Test
  public void handleOnFloor_direction() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevatorIndex, EventType.ELEVATOR_ARRIVE, 2, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, "scheduler", floor.getId());
    // Set the system status
    floor.getDirectionLamp(elevatorIndex).setDirection(Direction.DOWN);
    floor.getDirectionLamp(elevatorIndex).setActiveElevatorFloor(4);

    // Fire the event
    handler.handleOnFloor(testEvent, floor);

    assertEquals(Direction.UP, floor.getDirectionLamp(elevatorIndex).getDirection());
  }

  @Test
  public void handleOnFloor_activeElevatorFloor() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevatorIndex, EventType.ELEVATOR_ARRIVE, 6, Direction.DOWN);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, "scheduler", floor.getId());
    // Set the system status
    floor.getDirectionLamp(elevatorIndex).setDirection(Direction.UP);
    floor.getDirectionLamp(elevatorIndex).setActiveElevatorFloor(33);

    // Fire the event
    handler.handleOnFloor(testEvent, floor);

    assertEquals(6, floor.getDirectionLamp(elevatorIndex).getActiveElevatorFloor());
  }

  @Test
  public void handleOnScheduler_hasFloorRequest_shouldCommandElevatorStop() {
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevatorIndex, EventType.ELEVATOR_ARRIVE, 2, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, elevator.getId(), schedulerSubsystem.getId());

    // Set the system status
    schedulerSubsystem.getControllerBoard().setFloorRequests(Direction.UP, 2, true);

    // Fire the event
    handler.handleOnScheduler(testEvent, schedulerSubsystem);

    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 2,
            Direction.UP));
    assertFalse(schedulerSubsystem.getControllerBoard().hasFloorRequest(Direction.UP, 2));
  }

  @Test
  public void handleOnScheduler_hasElevatorRequest_shouldCommandElevatorStop() {
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevatorIndex, EventType.ELEVATOR_ARRIVE, 2, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, elevator.getId(), schedulerSubsystem.getId());
    // Set the system status
    schedulerSubsystem.getControllerBoard().setFloorRequests(Direction.UP, 2, true);
    schedulerSubsystem.getControllerBoard().setElevatorRequest(elevator.getElevatorIndex(), 2, Direction.UP);

    // Verify SetElevatorRequest is on
    assertTrue(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 2,
            Direction.UP));

    // Fire the event
    handler.handleOnScheduler(testEvent, schedulerSubsystem);

    // Verify SetElevatorRequest is on
    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(2, 1, Direction.UP));
  }

  @Test
  public void handleOnScheduler_noRequest_keepMove() {
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevatorIndex, EventType.ELEVATOR_ARRIVE, 2, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, elevator.getId(), schedulerSubsystem.getId());
    // Set the system status

    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 2,
            Direction.UP));

    // Fire the event
    handler.handleOnScheduler(testEvent, schedulerSubsystem);

    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 2,
            Direction.UP));
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
    floorSubsystem.close();
  }
}
