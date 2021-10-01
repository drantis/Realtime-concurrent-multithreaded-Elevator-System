package sysc.g1.scheduler;

import org.junit.Test;
import sysc.g1.utils.Direction;

import static org.junit.Assert.*;

public class ControllerBoardTest {
	
  // number of Floor or Elevator
  private int numberOfFloor = 10;	
  private int numberOfElevator = 1;

  @Test
  public void getMinMaxFloorRequestTest_shouldPass() {
    ControllerBoard controllerBoard = new ControllerBoard(numberOfFloor, numberOfElevator);
    controllerBoard.setFloorRequests(Direction.UP, 2, true);
    controllerBoard.setFloorRequests(Direction.UP, 5, true);
    controllerBoard.setFloorRequests(Direction.UP, 4, true);
    controllerBoard.setFloorRequests(Direction.UP, 8, true);

    controllerBoard.setFloorRequests(Direction.DOWN, 10, true);

    int[] upMinMax = controllerBoard.getMinMaxFloorRequest(Direction.UP);
    assertEquals(2, upMinMax[0]);
    assertEquals(8, upMinMax[1]);

    int[] downMinMax = controllerBoard.getMinMaxFloorRequest(Direction.DOWN);
    assertEquals(10, downMinMax[0]);
    assertEquals(10, downMinMax[1]);
  }

  @Test
  public void getMinMaxFloorRequestTest_no_requests() {
    ControllerBoard controllerBoard = new ControllerBoard(numberOfFloor, numberOfElevator);

    int[] upMinMax = controllerBoard.getMinMaxFloorRequest(Direction.UP);
    assertEquals(-1, upMinMax[0]);
    assertEquals(-1, upMinMax[1]);

    int[] downMinMax = controllerBoard.getMinMaxFloorRequest(Direction.DOWN);
    assertEquals(-1, downMinMax[0]);
    assertEquals(-1, downMinMax[1]);
  }

  @Test
  public void costToGo_simple_case() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 2);
    controllerBoard.setElevatorLocation(1, 20);
    controllerBoard.setElevatorDirection(1, Direction.UP);

    assertEquals(30, controllerBoard.costToGo(50, Direction.UP, 1));
  }

  @Test
  public void costToGo_simple_case_with_waiting() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 2);
    controllerBoard.setElevatorLocation(1, 20);
    controllerBoard.setElevatorDirection(1, Direction.UP);
    controllerBoard.setElevatorRequest(1, 30, Direction.UP);
    controllerBoard.setElevatorRequest(1, 32, Direction.UP);

    assertEquals(38, controllerBoard.costToGo(50, Direction.UP, 1));
  }

  @Test
  public void costToGo_simple_case_down() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 2);
    controllerBoard.setElevatorLocation(2, 50);
    controllerBoard.setElevatorDirection(2, Direction.DOWN);

    assertEquals(30, controllerBoard.costToGo(20, Direction.DOWN, 2));
  }

  @Test
  public void costToGo_simple_case_with_waiting_down() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 2);
    controllerBoard.setElevatorLocation(2, 50);
    controllerBoard.setElevatorDirection(2, Direction.DOWN);
    controllerBoard.setElevatorRequest(2, 30, Direction.DOWN);
    controllerBoard.setElevatorRequest(2, 32, Direction.DOWN);

    assertEquals(38, controllerBoard.costToGo(20, Direction.DOWN, 2));
  }

  @Test
  public void costToGo_simple_case_with_waiting_down_2_elevators() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 2);
    controllerBoard.setElevatorLocation(2, 50);
    controllerBoard.setElevatorDirection(2, Direction.DOWN);
    controllerBoard.setElevatorRequest(2, 30, Direction.DOWN);
    controllerBoard.setElevatorRequest(2, 32, Direction.DOWN);

    assertEquals(38, controllerBoard.costToGo(20, Direction.DOWN, 2));
    assertEquals(19, controllerBoard.costToGo(20, Direction.DOWN, 1));
  }

  @Test
  public void costToGo_idle_elevators() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 5);
    controllerBoard.setElevatorLocation(1, 50);
    controllerBoard.setElevatorLocation(2, 30);
    controllerBoard.setElevatorLocation(3, 20);
    controllerBoard.setElevatorLocation(4, 70);
    controllerBoard.setElevatorLocation(5, 10);

    assertEquals(30, controllerBoard.costToGo(20, Direction.DOWN, 1));
    assertEquals(10, controllerBoard.costToGo(20, Direction.DOWN, 2));
    assertEquals(0, controllerBoard.costToGo(20, Direction.DOWN, 3));
    assertEquals(50, controllerBoard.costToGo(20, Direction.DOWN, 4));
    assertEquals(10, controllerBoard.costToGo(20, Direction.DOWN, 5));
  }

  @Test
  public void costToGo_changeDirection_upToDown() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 1);
    controllerBoard.setElevatorLocation(1, 50);
    controllerBoard.setElevatorDirection(1, Direction.UP);
    controllerBoard.setElevatorRequest(1, 70, Direction.UP);
    controllerBoard.setElevatorRequest(1, 80, Direction.UP);
    controllerBoard.setElevatorRequest(1, 40, Direction.DOWN);

    assertEquals(58, controllerBoard.costToGo(60, Direction.DOWN, 1));
    assertEquals(73, controllerBoard.costToGo(45, Direction.DOWN, 1));
    assertEquals(102, controllerBoard.costToGo(20, Direction.UP, 1));
  }

  @Test
  public void costToGo_changeDirection_downToUp() {
    ControllerBoard controllerBoard = new ControllerBoard(100, 1);
    controllerBoard.setElevatorLocation(1, 50);
    controllerBoard.setElevatorDirection(1, Direction.DOWN);
    controllerBoard.setElevatorRequest(1, 40, Direction.DOWN);
    controllerBoard.setElevatorRequest(1, 30, Direction.DOWN);

    assertEquals(43, controllerBoard.costToGo(45, Direction.UP, 1));
  }
}