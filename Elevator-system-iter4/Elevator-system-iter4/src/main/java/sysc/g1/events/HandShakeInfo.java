package sysc.g1.events;

import java.time.LocalTime;

/**
 * The Handshake event notifies subsystem about the starting clock time of the system
 */
public class HandShakeInfo extends EventInfo {
  private LocalTime startupTimestamp;
  private LocalTime realStartupTime;

  public HandShakeInfo(LocalTime start, LocalTime realStart) {
    this.startupTimestamp = start;
    this.realStartupTime = realStart;
  }

  public LocalTime getStartupTimestamp() {
    return startupTimestamp;
  }

  public LocalTime getRealStartupTime() {
    return realStartupTime;
  }

  @Override
  public String toString() {
    return "Hand shaking";
  }
}
