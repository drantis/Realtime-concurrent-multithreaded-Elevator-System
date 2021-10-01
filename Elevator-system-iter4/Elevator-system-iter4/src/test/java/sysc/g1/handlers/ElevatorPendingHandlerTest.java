package sysc.g1.handlers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sysc.g1.actors.ActorSimulator;
import sysc.g1.elevator.Elevator;
import sysc.g1.elevator.ElevatorSubsystem;
import sysc.g1.entity.EntityType;
import sysc.g1.events.*;
import sysc.g1.floor.Floor;
import sysc.g1.floor.FloorSubsystem;
import sysc.g1.scheduler.ControllerBoard;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.states.DoorState;
import sysc.g1.utils.Direction;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_IP;
import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_IP;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_PORT;

public class ElevatorPendingHandlerTest {
  private static ElevatorSubsystem elevatorSubsystem;
  private static SchedulerSubsystem schedulerSubsystem;
  private static Elevator elevator;

  private static ElevatorPendingHandler handler;
  
 //number of Floor or Elevator
 private static int numberOfFloor = 10;
 private static int numberOfElevator = 1;

  @BeforeClass
  public static void setUp() {
    handler = new ElevatorPendingHandler();
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
  public void getNextEventTypeTest_no_requests() {
    EventType nextMove;

    // when controllerBoard has no request
    ControllerBoard b1 = new ControllerBoard(numberOfFloor, numberOfElevator);
    ElevatorInfo info_1 = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_PENDING, 2, Direction.UP);
    nextMove = handler.getNextEventType(b1, info_1);
    assertEquals(nextMove, EventType.ELEVATOR_DEACTIVATE);
  }

  @Test
  public void getNextEventTypeTest_keep_moving_up() {
    EventType nextMove;

    // new UP request at higher floor
    ControllerBoard b2 = new ControllerBoard(numberOfFloor, numberOfElevator);
    b2.setElevatorDirection(elevator.getElevatorIndex(), Direction.UP);
    b2.setElevatorLocation(elevator.getElevatorIndex(), 3);
    b2.setElevatorRequest(elevator.getElevatorIndex(), 5, Direction.UP);
    ElevatorInfo info_2 = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_PENDING, 3, Direction.UP);
    nextMove = handler.getNextEventType(b2, info_2);
    assertEquals(nextMove, EventType.ELEVATOR_UP);

    //  elevator's previous direction is UP at level 4
    //  There are UP requests at level 1 and 2
    //  There are DOWN requests at level 7 and 9
    // => should move UP
    ControllerBoard b3 = new ControllerBoard(numberOfFloor, numberOfElevator);
    b3.setElevatorDirection(elevator.getElevatorIndex(), Direction.UP);
    b3.setElevatorLocation(elevator.getElevatorIndex(), 4);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 1, Direction.DOWN);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 2, Direction.DOWN);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 7, Direction.UP);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 9, Direction.UP);
    ElevatorInfo info_3 = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_PENDING, 4, Direction.UP);

    nextMove = handler.getNextEventType(b3, info_3);
    assertEquals(nextMove, EventType.ELEVATOR_UP);
  }

  @Test
  public void getNextEventTypeTest_keep_moving_down() {
    EventType nextMove;

    // new DOWN request at lower floor
    ControllerBoard b2 = new ControllerBoard(numberOfFloor, numberOfElevator);
    b2.setElevatorDirection(elevator.getElevatorIndex(), Direction.DOWN);
    b2.setElevatorLocation(elevator.getElevatorIndex(), 4);
    b2.setElevatorRequest(elevator.getElevatorIndex(), 2, Direction.DOWN);
    b2.setFloorRequests(Direction.DOWN, 2, true);
    ElevatorInfo info_2 = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_PENDING, 4, Direction.DOWN);
    nextMove = handler.getNextEventType(b2, info_2);
    assertEquals(nextMove, EventType.ELEVATOR_DOWN);

    //  elevator's previous direction is DOWN at level 4
    //  There are UP requests at level 1 and 2
    //  There are DOWN requests at level 3, 7 and 9
    // => should move DOWN
    ControllerBoard b3 = new ControllerBoard(numberOfFloor, numberOfElevator);
    b3.setElevatorDirection(elevator.getElevatorIndex(), Direction.DOWN);
    b3.setElevatorLocation(elevator.getElevatorIndex(), 4);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 2, Direction.DOWN);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 1, Direction.DOWN);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 3, Direction.DOWN);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 7, Direction.UP);
    b3.setElevatorRequest(elevator.getElevatorIndex(), 9, Direction.UP);
    ElevatorInfo info_3 = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_PENDING, 4, Direction.DOWN);

    nextMove = handler.getNextEventType(b3, info_3);
    assertEquals(nextMove, EventType.ELEVATOR_DOWN);
  }


  @Test
  public void getNextEventTypeTest_switch_up_to_down() {
    EventType nextMove;

    //  elevator's previous direction is UP at level 4
    //  There are UP requests at level 1 and 2
    //  There are DOWN requests at level 3
    // => should move DOWN
    ControllerBoard b4 = new ControllerBoard(numberOfFloor, numberOfElevator);
    b4.setElevatorDirection(elevator.getElevatorIndex(), Direction.UP);
    b4.setElevatorLocation(elevator.getElevatorIndex(), 4);
    b4.setElevatorRequest(elevator.getElevatorIndex(), 1, Direction.DOWN);
    b4.setElevatorRequest(elevator.getElevatorIndex(), 2, Direction.DOWN);
    b4.setElevatorRequest(elevator.getElevatorIndex(), 3, Direction.DOWN);
    ElevatorInfo info_4 = new ElevatorInfo(elevator.getElevatorIndex(),EventType.ELEVATOR_PENDING, 4, Direction.UP);

    nextMove = handler.getNextEventType(b4, info_4);
    assertEquals(nextMove, EventType.ELEVATOR_DOWN);

    //  elevator's previous direction is UP at level 4
    //  There are DOWN requests at level 2 and 3
    // => should move DOWN
    ControllerBoard b5 = new ControllerBoard(numberOfFloor, numberOfElevator);
    ElevatorInfo info_5 = new ElevatorInfo(elevator.getElevatorIndex(), EventType.ELEVATOR_PENDING, 4, Direction.UP);
    b5.setElevatorDirection(elevator.getElevatorIndex(), Direction.UP);
    b5.setElevatorLocation(elevator.getElevatorIndex(), 4);
    b5.setElevatorRequest(elevator.getElevatorIndex(), 2, Direction.DOWN);
    b5.setElevatorRequest(elevator.getElevatorIndex(), 3, Direction.DOWN);
    b5.setFloorRequests(Direction.UP, 2, true);
    b5.setFloorRequests(Direction.UP, 3, true);

    nextMove = handler.getNextEventType(b5, info_5);
    assertEquals(nextMove, EventType.ELEVATOR_DOWN);
  }

  @AfterClass
  public static void tearDown() {
    elevatorSubsystem.close();
    schedulerSubsystem.close();
  }
}