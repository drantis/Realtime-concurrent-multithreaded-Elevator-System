package sysc.g1.utils;

import sysc.g1.events.EventInfo;
import sysc.g1.exceptions.InvalidEventTypeException;

public class EventParser {
  public static <T extends EventInfo> T parse(Class<T> clazz, EventInfo info)
      throws InvalidEventTypeException {
    if (!clazz.isInstance(info)) {
      throw new InvalidEventTypeException(
          String.format("Require event of type: %s, but: %s", clazz, info.getClass()));
    }

    return clazz.cast(info);
  }
}
