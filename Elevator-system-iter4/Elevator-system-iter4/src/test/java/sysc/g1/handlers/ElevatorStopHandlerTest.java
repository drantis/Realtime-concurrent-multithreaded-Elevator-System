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
import sysc.g1.events.CommandInfo;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.ElevatorState;
import sysc.g1.states.FloorState;
import sysc.g1.states.LampState;
import sysc.g1.states.MotorState;
import sysc.g1.utils.Direction;

public class ElevatorStopHandlerTest {

  private static Floor floor;
  private static FloorSubsystem floorSubsystem;
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Elevator elevator;
  private static ElevatorStopHandler handler;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorStopHandler();
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
  public void handleOnFloor_succeeded() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_STOP, 4, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, "scheduler", floor.getId());
    // Set the system status
    floor.getLamp(Direction.UP).switchLamp(LampState.ON);
    floor.addState(FloorState.PENDING_DOWN);
    floor.addState(FloorState.PENDING_UP);

    // Fire the event
    handler.handleOnFloor(testEvent, floor);

    // Verify that it is good
    assertEquals(LampState.OFF, floor.getLamp(Direction.UP).getState());
    assertTrue(floor.getActiveStates().contains(FloorState.PENDING_DOWN));
    assertFalse(floor.getActiveStates().contains(FloorState.PENDING_UP));
  }

  @Test
  public void handleOnFloor_continue_off() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_STOP, 4, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, "scheduler", floor.getId());
    // Set the system status
    floor.getLamp(Direction.UP).switchLamp(LampState.OFF);

    // Fire the event
    handler.handleOnFloor(testEvent, floor);

    // Verify that it is good
    assertEquals(LampState.OFF, floor.getLamp(Direction.UP).getState());
  }

  @Test
  public void handleOnFloor_ignored() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_STOP, 3, Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, "scheduler", floor.getId());
    // Set the system status
    floor.getLamp(Direction.UP).switchLamp(LampState.ON);

    // Fire the event
    handler.handleOnFloor(testEvent, floor);

    // Verify that it is good
    assertEquals(LampState.ON, floor.getLamp(Direction.UP).getState());
  }

  @Test
  public void schedulerSendStopStatusToFloor() {
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_STOP, 5, Direction.NONE);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    handler.handleOnScheduler(testEvent, schedulerSubsystem);
  }

  @Test
  public void handleOnElevator_successful() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_STOP, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    elevator.setCurrFloor(4);
    elevator.getMotor().setMotorState(MotorState.CONSTANT_SPEED);
    elevator.setCurrDirection(Direction.UP);

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // Verify that it is good
    assertEquals(4, elevator.getCurrFloor());
    assertEquals(MotorState.DECELLERATING, elevator.getMotor().getMotorState());
    assertEquals(Direction.UP, elevator.getCurrDirection());
    assertTrue(elevator.getStates().contains(ElevatorState.LOADING));
  }

  @Test
  public void handleOnElevator_wrong_floor() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_STOP, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    elevator.setCurrFloor(3);
    elevator.getMotor().setMotorState(MotorState.CONSTANT_SPEED);
    elevator.setCurrDirection(Direction.UP);

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    assertEquals(3, elevator.getCurrFloor());
    assertEquals(MotorState.CONSTANT_SPEED, elevator.getMotor().getMotorState());
    assertEquals(Direction.UP, elevator.getCurrDirection());
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
    floorSubsystem.close();
  }
}