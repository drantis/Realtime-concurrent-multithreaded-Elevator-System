package sysc.g1.scheduler;

import java.time.Duration;
import java.util.Arrays;
import sysc.g1.utils.Direction;

public class ControllerBoard {
  boolean[] upFloorRequests;
  boolean[] downFloorRequests;
  private int totalFloors;
  private int totalElevators;
  private Direction[] elevatorDirections;
  private int[] elevatorLocations;
  private ElevatorTrip[] elevatorTrips;
  private boolean[] disabled;

  public ControllerBoard(int totalFloors, int totalElevators) {
    this.upFloorRequests = new boolean[totalFloors];
    this.downFloorRequests = new boolean[totalFloors];

    this.elevatorLocations = new int[totalElevators];
    this.elevatorTrips = new ElevatorTrip[totalElevators];
    this.elevatorDirections = new Direction[totalElevators];
    this.disabled = new boolean[totalElevators];

    for (int i=0; i<totalElevators; i++) {
      elevatorTrips[i] = new ElevatorTrip(totalFloors);
      elevatorLocations[i] =  1;
      elevatorDirections[i] = Direction.NONE;
      disabled[i] = false;
    }

    this.totalFloors = totalFloors;
    this.totalElevators = totalElevators;
  }

  public ControllerBoard(int totalFloors, int totalElevators, int[] initialLocation) {
    this(totalFloors, totalElevators);
    this.elevatorLocations = initialLocation;
  }

  public Direction[] getElevatorDirections() {
    return elevatorDirections;
  }

  public void setElevatorDirections(int elevator, Direction dir) {
    this.elevatorDirections[elevator-1] = dir;
  }

  public void setFloorRequests(Direction direction, int floor, boolean on) {
    if (direction == Direction.UP) {
      this.upFloorRequests[floor - 1] = on;
    }else {
      this.downFloorRequests[floor - 1] = on;
    }
  }

  public void disableElevator(int elevatorId) {
    this.disabled[elevatorId - 1] = true;
  }

  public void enableElevator(int elevatorId) {
    this.disabled[elevatorId -1] = false;
  }

  public void setElevatorRequest(int elevatorId, int floorRequest, Direction dir){
    elevatorTrips[elevatorId-1].setRequest(floorRequest, dir, true);
  }

  public boolean hasElevatorRequest(int elevatorId, int floorRequest, Direction dir){
    return elevatorTrips[elevatorId-1].getRequest(floorRequest, dir);
  }

  public void clearRequest(int floorNum, Direction dir){
    for (ElevatorTrip trip: elevatorTrips){
      trip.setRequest(floorNum, dir, false);
    }
    if (dir == Direction.UP) {
      upFloorRequests[floorNum-1] = false;
    } else {
      downFloorRequests[floorNum-1] = false;
    }
  }

  public Direction getElevatorDirection(int id) {
    return elevatorDirections[id-1];
  }

  public int getElevatorLocation(int id) {
    return elevatorLocations[id -1];
  }

  public void setElevatorDirection(int elevatorId, Direction direction) {
    elevatorDirections[elevatorId-1] = direction;
  }

  public void setElevatorLocation(int elevatorId, int floor) {
    elevatorLocations[elevatorId-1] = floor;
  }

  public boolean hasFloorRequest(Direction direction, int floor) {
    if (direction == Direction.UP) {
      return this.upFloorRequests[floor - 1];
    }
    return this.downFloorRequests[floor - 1];
  }

  public int getLastFloorToGo(Direction dir) {
    int highest = 1;
    int lowest = totalFloors;

    if (dir == Direction.UP) {
      for (int i = totalFloors; i >= 1; i--) {
        if (this.upFloorRequests[i - 1] || this.downFloorRequests[i - 1]) {
          return i;
        }
      }
    } else {
      for (int i = 1; i <= totalFloors; i++) {
        if (this.upFloorRequests[i - 1] || this.downFloorRequests[i - 1]) {
          return i;
        }
      }
    }
    // No request at all????
    return -1;
  }

  public int[] getMinMaxFloorRequest(Direction direction) {
    int[] minMax = {-1, -1};
    for (int i = 1; i <= totalFloors; i++) {
      if (hasFloorRequest(direction, i)) {
        minMax[0] = minMax[0] == -1 ? i : minMax[0];
        minMax[1] = i;
      }
    }
    return minMax;
  }

  public boolean[] getUpFloorRequests() {
    return upFloorRequests;
  }

  public boolean[] getDownFloorRequests() {
    return downFloorRequests;
  }

  public int getTotalFloors() {
    return totalFloors;
  }

  public int getTotalElevators() {
    return totalElevators;
  }

  public Direction getNextDirection(int elevator) {
    Direction pendingElevatorDirection = elevatorDirections[elevator-1];
    int pendingFloor = elevatorLocations[elevator-1];
    ElevatorTrip trip = elevatorTrips[elevator-1];
    // If the elevator reaches the first or the top floor, need to change the pending direction
    if (pendingFloor == 1 || pendingFloor == totalFloors) {
      pendingElevatorDirection = pendingFloor == 1 ? Direction.UP : Direction.DOWN;
    }

    boolean[] onTheWayRequest = Arrays.copyOfRange(trip.getUpRequests(), pendingFloor, totalFloors);
    boolean[] onTheWayRequest2 = Arrays.copyOfRange(trip.getDownRequests(), pendingFloor, totalFloors);
    boolean[] oppositeWayRequest = Arrays.copyOfRange(trip.getDownRequests(), 0, pendingFloor-1);
    boolean[] oppositeWayRequest2 = Arrays.copyOfRange(trip.getUpRequests(), 0, pendingFloor-1);
    if (pendingElevatorDirection == Direction.DOWN) {
      onTheWayRequest = Arrays.copyOfRange(trip.getDownRequests(), 0, pendingFloor-1);
      onTheWayRequest2 = Arrays.copyOfRange(trip.getUpRequests(), 0, pendingFloor-1);
      oppositeWayRequest = Arrays.copyOfRange(trip.getUpRequests(), pendingFloor, totalFloors);
      oppositeWayRequest2 = Arrays.copyOfRange(trip.getDownRequests(), pendingFloor, totalFloors);
    }

    // Check for requests on the way of the current direction
    for (boolean req: onTheWayRequest) {
      if (req) {
        return pendingElevatorDirection == Direction.NONE ? Direction.UP : pendingElevatorDirection;
      }
    }
    for (boolean req: onTheWayRequest2) {
      if (req) {
        return pendingElevatorDirection == Direction.NONE ? Direction.UP : pendingElevatorDirection;
      }
    }

    // No elevator request in the current direction -> check for pending floor request that come from
    // a higher floor and NO other elevator is doing it
    for (boolean req: oppositeWayRequest) {
      if (req) {
        return pendingElevatorDirection == Direction.NONE ? Direction.DOWN : getOppositeDirection(pendingElevatorDirection);
      }
    }

    for (boolean req: oppositeWayRequest2) {
      if (req) {
        return pendingElevatorDirection == Direction.NONE ? Direction.DOWN : getOppositeDirection(pendingElevatorDirection);
      }
    }

    return Direction.NONE;
  }

  public Direction getOppositeDirection(Direction dir) {
    return dir == Direction.UP ? Direction.DOWN : Direction.UP;
  }

  private boolean hasAnyRequest(int elevatorId, int from, Direction searchDirection) {
    int step = searchDirection == Direction.UP ? 1 : -1;
    for (int i=from+step; i>=1 && i<=totalFloors; i+=step) {
      if (elevatorTrips[elevatorId -1].getRequest(i, searchDirection)) return true;
    }
    return false;
  }

  private boolean shouldChangeDirection(int elevator, Direction curDir, int curLoc, int target, Direction targetDir) {
    if ((curDir == Direction.UP && target > curLoc) || (curDir == Direction.DOWN && target < curLoc)) {
      return false;
    }

    if (target == curLoc && targetDir == curDir) return false;

    return !hasAnyRequest(elevator, curLoc, curDir);
  }

  public int costToGo(int floor, Direction floorRequestDirection, int elevator) {
    int elevatorLocation = elevatorLocations[elevator-1];
    Direction elevatorDirection = elevatorDirections[elevator-1];
    // step = 1 if going up, -1 if going down
    if (elevatorDirection == Direction.NONE) {
      return Math.abs(floor - elevatorLocation);
    }

    int curLoc = elevatorLocation;
    Direction curDir = elevatorDirection;

    int cost = 0;
    while (curLoc != floor || curDir != floorRequestDirection) {
      int step = curDir == Direction.UP ? 1 : -1;
      curLoc += step;
      int loadingCost = elevatorTrips[elevator-1].getRequest(curLoc, curDir)? 4 : 0;
      cost += Math.abs(step) + loadingCost;
      curDir = shouldChangeDirection(elevator, curDir, curLoc, floor, floorRequestDirection) ?
          getOppositeDirection(curDir) : curDir;

    }
    return cost;
  }

  public int findBestElevator(int floor, Direction floorRequestDirection) {
    int min = Integer.MAX_VALUE;
    int best = 1;
    for (int i=1; i<=totalElevators; i++) {
      if (disabled[i-1]) {
        continue;
      }
      int cost = costToGo(floor, floorRequestDirection, i);
      if (cost < min) {
        min = cost;
        best = i;
      }
    }
    return best;
  }

  public boolean hasFurtherRequest(int elevatorIndex, int arrivingFloor, Direction dir) {
    ElevatorTrip trip = this.elevatorTrips[elevatorIndex-1];
    boolean[] sameDirectionReqs;
    boolean[] oppositeDirectionReqs;
    if (dir == Direction.UP) {
      sameDirectionReqs = Arrays.copyOfRange(trip.getUpRequests(), arrivingFloor, totalFloors);
      oppositeDirectionReqs = Arrays.copyOfRange(trip.getDownRequests(), arrivingFloor, totalFloors);
    } else {
      sameDirectionReqs = Arrays.copyOfRange(trip.getUpRequests(), 0, arrivingFloor-1);
      oppositeDirectionReqs = Arrays.copyOfRange(trip.getDownRequests(), 0, arrivingFloor-1);
    }

    for (boolean b: sameDirectionReqs){
      if (b) return true;
    }

    for (boolean b: oppositeDirectionReqs){
      if (b) return true;
    }
    return false;
  }
}
