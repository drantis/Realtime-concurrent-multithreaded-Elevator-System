package sysc.g1.events;

public class EventParser {

  public static <T extends EventInfo> T parseInfo(Class<T> clazz, EventInfo info) {
    if (clazz.isInstance(info)) {
      return clazz.cast(info);
    }
    throw new ClassCastException("Invalid event info object");
  }

}
