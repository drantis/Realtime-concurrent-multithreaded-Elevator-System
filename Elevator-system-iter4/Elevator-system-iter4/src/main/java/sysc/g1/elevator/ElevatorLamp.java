package sysc.g1.elevator;

import sysc.g1.states.LampState;

public class ElevatorLamp {
  private int level;
  private LampState state;

  public ElevatorLamp(int level) {
    this.level = level;
    this.state = LampState.OFF;
  }

  public void setState(LampState state) {
    this.state = state;
  }

  public LampState getState() {
    return state;
  }

//  public void switchOn() {
//    this.state = LampState.ON;
//  }
//
//  public void switchOff() {
//    this.state = LampState.OFF;
//  }
}
