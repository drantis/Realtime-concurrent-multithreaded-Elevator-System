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
import sysc.g1.utils.Direction;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;

public class ElevatorContinuePassingHandlerTest {
  private static ElevatorSubsystem elevatorSubsystem;
  private static Elevator elevator;
  private static ElevatorContinuePassingHandler handler;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorContinuePassingHandler();
    //number of Floor or Elevator
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

    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_CONTINUE_PASSING, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // Set the system status
    elevator.setCurrFloor(4);
    elevator.setCurrDirection(Direction.UP);
    elevator.getMotor().setMotorState(MotorState.ACCELERATING);

    assertEquals(4, elevator.getCurrFloor());

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    assertEquals(5, elevator.getCurrFloor());
    assertEquals(MotorState.CONSTANT_SPEED, elevator.getMotor().getMotorState());
  }

  @Test
  public void handleOnElevator_ignores_when_door_open() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);


    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_CONTINUE_PASSING, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // Set the system status
    elevator.setCurrFloor(4);
    elevator.setCurrDirection(Direction.UP);
    elevator.getDoor().setState(DoorState.OPEN);
    elevator.getMotor().setMotorState(MotorState.ACCELERATING);

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // handler ignores event
    assertEquals(4, elevator.getCurrFloor());
    assertEquals(MotorState.ACCELERATING, elevator.getMotor().getMotorState());
  }

  @Test
  public void handleOnElevator_ignores_wrong_motor_state() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);

    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_CONTINUE_PASSING, 4, elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    // Set the system status
    elevator.setCurrFloor(4);
    elevator.setCurrDirection(Direction.UP);
    elevator.getMotor().setMotorState(MotorState.IDLING);

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // handler ignores event
    assertEquals(4, elevator.getCurrFloor());
    assertEquals(MotorState.IDLING, elevator.getMotor().getMotorState());
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
  }
}