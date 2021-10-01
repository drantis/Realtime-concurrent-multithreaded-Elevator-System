package sysc.g1.utils;

import java.time.Duration;

public class TimeUtil {
  private static ConfigParser config = new ConfigParser();

  public static Duration SWITCH_LAMP_DURATION = Duration.ofMillis(config.getIntegerProperty("switch_lamp_duration"));

  public static Duration DOOR_OPEN_DURATION = Duration.ofMillis(config.getIntegerProperty("door_open_duration"));
  public static Duration DOOR_CLOSE_DURATION = Duration.ofMillis(config.getIntegerProperty("door_close_duration"));
  public static Duration MOVE_ONE_FLOOR_DURATION = Duration.ofMillis(config.getIntegerProperty(
          "move_one_floor_duration"));
  public static Duration CONTINUE_ONE_FLOOR = Duration.ofMillis(config.getIntegerProperty("continue_one_floor"));
  public static Duration FLOOR_LOADING_DURATION = Duration.ofMillis(config.getIntegerProperty("floor_loading_duration"
  ));
}
