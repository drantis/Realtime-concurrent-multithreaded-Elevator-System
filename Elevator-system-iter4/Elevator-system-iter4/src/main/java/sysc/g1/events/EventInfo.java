package sysc.g1.events;

import java.io.Serializable;

public abstract class EventInfo implements Serializable {
  protected EventType type;

  public EventType getType() {
    return this.type;
  }

  public String toString() {
    return String.format("%s", type);
  }
}
