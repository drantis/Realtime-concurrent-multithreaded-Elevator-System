package sysc.g1.handlers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sysc.g1.actors.ActorSimulator;
import sysc.g1.elevator.Elevator;
import sysc.g1.elevator.ElevatorSubsystem;
import sysc.g1.entity.EntityType;
import sysc.g1.events.FloorResponseInfo;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventType;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.states.LampState;
import sysc.g1.utils.Direction;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;
import static sysc.g1.network.SubsystemPorts.FLOOR_IP;
import static sysc.g1.network.SubsystemPorts.FLOOR_PORT;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_IP;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_PORT;

public class ElevatorDoorOpenHandlerTest {

  private static Floor floor;
  private static FloorSubsystem floorSubsystem;
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Elevator elevator;

  private static ElevatorDoorOpenHandler handler;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorDoorOpenHandler();
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
    floor = floorSubsystem.getFloor(9);
    elevator = elevatorSubsystem.getElevator(1);
  }

  @Test
  public void schedulerPassSuccessful() {
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_DOOR_OPEN, 5,
            Direction.NONE);
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    handler.handleOnScheduler(testEvent, schedulerSubsystem);
  }

  @Test
  public void handleOnFloor_succeeded() {

    // Build the event
    LocalTime now = LocalTime.of(3, 0);
    ElevatorInfo testInfo = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_DOOR_OPEN, 4,
            Direction.UP);
    Event testEvent = new Event(now, EntityType.ELEVATOR, testInfo, "scheduler", floor.getId());

    // Fire the event
    handler.handleOnFloor(testEvent, floor);

    // Verify that it is good
    assertEquals(EventType.FLOOR_LOADING_FINISH, EventType.FLOOR_LOADING_FINISH);
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
    floorSubsystem.close();
  }
}