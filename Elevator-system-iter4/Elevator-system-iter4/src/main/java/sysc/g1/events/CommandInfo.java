package sysc.g1.events;

public class CommandInfo extends  EventInfo{
  private int level;
  private int elevator;
  public CommandInfo(EventType type, int level, int elevator) {
    this.type = type;
    this.level = level;
    this.elevator = elevator;
  }

  public int getElevator() {
    return elevator;
  }

  public int getLevel() {
    return level;

  }
}


