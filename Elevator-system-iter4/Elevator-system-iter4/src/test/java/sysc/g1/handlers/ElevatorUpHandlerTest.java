package sysc.g1.handlers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sysc.g1.actors.ActorSimulator;
import sysc.g1.elevator.Elevator;
import sysc.g1.elevator.ElevatorSubsystem;
import sysc.g1.entity.EntityType;
import sysc.g1.events.CommandInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.MotorState;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;

public class ElevatorUpHandlerTest {
  private static ElevatorSubsystem elevatorSubsystem;
  private static Elevator elevator;
  private static ElevatorUpHandler handler;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorUpHandler();
    // number of Floor or Elevator
    int numberOfFloor = 10;
    int numberOfElevator = 1;
    elevatorSubsystem = new ElevatorSubsystem(
        numberOfFloor,
        numberOfElevator,
        ELEVATOR_IP,
        ELEVATOR_PORT);
    elevator = elevatorSubsystem.getElevator(1);
  }

  @Test
  public void handleOnElevator_succeeded() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_UP, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // Set the system status
    elevator.setCurrFloor(4);
    elevator.getDoor().setState(DoorState.CLOSED);
    assertEquals(4, elevator.getCurrFloor());
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // Verify that it is good
    assertEquals(5, elevator.getCurrFloor());
    assertEquals(MotorState.ACCELERATING, elevator.getMotor().getMotorState());
  }

  @Test
  public void handleOnElevator_ignored_wrong_floor() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_UP, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // elevator floor != floor
    elevator.setCurrFloor(3);
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // Verify that it is good
    assertEquals(3, elevator.getCurrFloor());
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());
  }

  @Test
  public void handleOnElevator_ignored_door_open() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);

    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_UP, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // elevator floor != floor
    elevator.setCurrFloor(4);
    elevator.getMotor().setMotorState(MotorState.IDLING);

    //DOOR still OPEN !!
    elevator.getDoor().setState(DoorState.OPEN);
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // Verify that it is good
    assertEquals(4, elevator.getCurrFloor());
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());
  }

  @Test
  public void handleOnElevator_ignored_wrong_event() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);

    //WRONG event type

    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_CONTINUE_PASSING, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // elevator floor != floor
    elevator.setCurrFloor(4);
    elevator.getDoor().setState(DoorState.OPEN);
    elevator.getMotor().setMotorState(MotorState.IDLING);
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // Verify that it is good
    assertEquals(4, elevator.getCurrFloor());
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());
    assertEquals(DoorState.OPEN, elevator.getDoor().getState());
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
  }
}