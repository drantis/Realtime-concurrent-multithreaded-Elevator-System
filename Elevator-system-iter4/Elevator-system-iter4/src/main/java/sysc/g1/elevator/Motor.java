package sysc.g1.elevator;

import sysc.g1.states.MotorState;

public class Motor {
  private MotorState motorState;

  public Motor() {
    this.motorState = MotorState.IDLING;
  }

  public MotorState getMotorState() {
    return motorState;
  }

  public void setMotorState(MotorState motorState) {
    this.motorState = motorState;
  }
}
