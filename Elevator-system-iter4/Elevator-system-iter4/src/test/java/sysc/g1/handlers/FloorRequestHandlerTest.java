package sysc.g1.handlers;

import static org.junit.Assert.*;
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
import sysc.g1.events.FloorRequestInfo;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.LampState;
import sysc.g1.utils.Direction;

public class FloorRequestHandlerTest {

  private static SchedulerSubsystem schedulerSubsystem;

  private static Handler handler;

  @BeforeClass
  public static void setUp() {
    handler = new FloorRequestHandler();
    // number of Floor or Elevator
    int numberOfFloor = 10;
    int numberOfElevator = 1;
    schedulerSubsystem = new SchedulerSubsystem(
        numberOfFloor,
        numberOfElevator,
        SCHEDULER_IP,
        SCHEDULER_PORT);
  }

  @Test
  public void handleOnScheduler_succeeded() {
    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    FloorRequestInfo testInfo = new FloorRequestInfo(Direction.UP, 4);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "actor", schedulerSubsystem.getId());
    // Set the system status
    assertFalse(schedulerSubsystem.getControllerBoard().hasFloorRequest(Direction.UP, 4));

    // Fire the event
    handler.handleOnScheduler(testEvent, schedulerSubsystem);

    assertTrue(schedulerSubsystem.getControllerBoard().hasFloorRequest(Direction.UP, 4));
  }

  @AfterClass
  public static void tearDown() {
    schedulerSubsystem.close();
  }
}