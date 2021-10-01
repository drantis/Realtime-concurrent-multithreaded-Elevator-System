package sysc.g1.handlers;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;

import java.time.LocalTime;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sysc.g1.actors.ActorSimulator;
import sysc.g1.elevator.Elevator;
import sysc.g1.elevator.ElevatorSubsystem;
import sysc.g1.entity.EntityType;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.ElevatorState;
import sysc.g1.utils.Direction;

public class ElevatorActivateHandlerTest {

  private static Elevator elevator;
  private static ElevatorSubsystem elevatorSubsystem;
  private static Handler handler;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorActivateHandler();
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
  public void handleOnElevator_shouldActivate_andSendEvent() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_ACTIVATE, 2, 1);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", "elevator_1");

    assertFalse(elevator.isActive());

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    // Verify that it is good
    assertTrue(elevator.isActive());
  }

  @Test
  public void handleOnElevator_alreadyActivate_noEvent() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    CommandInfo testInfo = new CommandInfo(EventType.ELEVATOR_ACTIVATE, 0, 1);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", "elevator_1");
    elevator.setState(ElevatorState.LOADING);

    // Fire the event
    handler.handleOnElevator(testEvent, elevator);

    assertTrue(elevator.isActive());
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
  }
}