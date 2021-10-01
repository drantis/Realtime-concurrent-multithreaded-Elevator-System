package sysc.g1.events;

public class ElevatorErrorInfo extends EventInfo {
  private int elevatorId;
  private String errorType;
  private int duration;

  public ElevatorErrorInfo(int elevatorId, String errorType) {
    this.elevatorId = elevatorId;
    this.errorType = errorType;
    this.type = EventType.ELEVATOR_ERROR;
    this.duration = -1; // -1 mean the error is permanent and can't be fixed
  }

  public ElevatorErrorInfo(int elevatorId, String errorType, int duration) {
    this.elevatorId = elevatorId;
    this.errorType = errorType;
    this.type = EventType.ELEVATOR_ERROR;
    this.duration = duration;
  }

  public int getElevatorId() {
    return elevatorId;
  }

  public int getDuration() {
    return duration;
  }

  public String getErrorType() {
    return errorType;
  }
}
