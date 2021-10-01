package sysc.g1.handlers;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;
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
import sysc.g1.events.ElevatorRequestInfo;
import sysc.g1.events.Event;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.utils.Direction;

public class ElevatorRequestHandlerTest {

  private static Elevator elevator;
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Handler handler;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorRequestHandler();
    // number of Floor or Elevator
    int numberOfFloor = 10;
    int numberOfElevator = 1;
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
    elevator = elevatorSubsystem.getElevator(1);
  }

  @Test
  public void handleOnSchedulerTest_shouldPass() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorRequestInfo testInfo = new ElevatorRequestInfo(elevator.getElevatorIndex(), 2, 4, Direction.UP);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "elevator", schedulerSubsystem.getId());

    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 4,
            Direction.UP));
    // Set the system status
    handler.handleOnScheduler(testEvent, schedulerSubsystem);
    assertTrue(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 4,
            Direction.UP));
  }

  @Test
  public void handleOnSchedulerTest_shouldIgnore() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorRequestInfo testInfo = new ElevatorRequestInfo(elevator.getElevatorIndex(), 2, 2, Direction.UP);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "elevator", schedulerSubsystem.getId());

    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 2,
            Direction.UP));
    // Set the system status
    handler.handleOnScheduler(testEvent, schedulerSubsystem);
    assertFalse(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 2,
            Direction.UP));
  }

  @Test
  public void handleOnSchedulerTest_requestAlreadyExists() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorRequestInfo testInfo = new ElevatorRequestInfo(elevator.getElevatorIndex(), 2, 4, Direction.UP);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "elevator", schedulerSubsystem.getId());

    schedulerSubsystem.getControllerBoard().setElevatorRequest(elevator.getElevatorIndex(), 4, Direction.UP);
    assertTrue(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 4,
            Direction.UP));
    // Set the system status
    handler.handleOnScheduler(testEvent, schedulerSubsystem);
    assertTrue(schedulerSubsystem.getControllerBoard().hasElevatorRequest(elevator.getElevatorIndex(), 4,
            Direction.UP));
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
  }
}
