package sysc.g1.handlers;

import static org.junit.Assert.*;

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
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.utils.Direction;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_IP;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_PORT;

public class FloorLoadingFinishHandlerTest {
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Elevator elevator;

  private static FloorLoadingFinishHandler handler;

  @BeforeClass
  public static void setUp() {
    handler = new FloorLoadingFinishHandler();
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
  public void handleOnElevator_DoorClosed() {
    LocalTime now = LocalTime.of(3, 0);
    FloorResponseInfo testInfo = new FloorResponseInfo(EventType.FLOOR_LOADING_FINISH, Direction.NONE, 9,
            elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    elevator.setCurrFloor(9);

    handler.handleOnElevator(testEvent, elevator);

    assertEquals(DoorState.CLOSED, elevator.getDoor().getState());
  }

  @Test
  public void schedulerReceiveAndSendLoadingFinish() {
    LocalTime now = LocalTime.of(3, 0);
    FloorResponseInfo testInfo = new FloorResponseInfo(EventType.FLOOR_LOADING_FINISH, Direction.NONE, 9,
            elevator.getElevatorIndex());
    Event testEvent = new Event(now, EntityType.SCHEDULER, testInfo, "scheduler", elevator.getId());

    handler.handleOnScheduler(testEvent, schedulerSubsystem);
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
  }
}