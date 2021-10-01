package sysc.g1.scheduler;

import sysc.g1.utils.Direction;

public class ElevatorTrip {

  private boolean[] upRequests;
  private boolean[] downRequests;
  private int totalFloors;

  public ElevatorTrip(int totalFloors) {
    this.upRequests = new boolean[totalFloors];
    this.downRequests = new boolean[totalFloors];
    this.totalFloors = totalFloors;
  }

  public boolean getRequest(int floorNum, Direction dir) {
    return dir == Direction.UP ? upRequests[floorNum - 1] : downRequests[floorNum - 1];
  }

  public void setRequest(int floorNum, Direction dir, boolean flag) {
    if (dir == Direction.UP) {
      upRequests[floorNum - 1] = flag;
    } else {
      downRequests[floorNum - 1] = flag;
    }
  }

  public boolean[] getUpRequests() {
    return upRequests;
  }

  public boolean[] getDownRequests() {
    return downRequests;
  }
}
