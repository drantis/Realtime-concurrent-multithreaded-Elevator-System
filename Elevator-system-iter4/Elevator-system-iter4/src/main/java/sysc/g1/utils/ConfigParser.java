package sysc.g1.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;
import sysc.g1.entity.EntityType;

public class ConfigParser {

  private Properties prop;
  private final String path = "config.properties";

  public ConfigParser() {
    try (InputStream config = getClass().getClassLoader().getResourceAsStream(path)) {
      this.prop = new Properties();
      prop.load(config);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public int getIntegerProperty(String prop) {
    return Integer.parseInt(getProperty(prop));
  }

  public int[] getArrayProperty(String prop) {
    String[] list = getProperty(prop).split(",");
    return Arrays.stream(list).mapToInt(Integer::parseInt).toArray();
  }

  public String getProperty(String propName) {
    return this.prop.getProperty(propName);
  }

  public InetAddress getAddress(EntityType entityType) throws UnknownHostException {
    switch (entityType) {
      case ELEVATOR:
        return InetAddress.getByName(getProperty("elevator_system_address"));
      case FLOOR:
        return InetAddress.getByName(getProperty("floor_system_address"));
      default:
        return InetAddress.getByName(getProperty("scheduler_system_address"));
    }
  }

  public int getPort(EntityType entityType) {
    switch (entityType) {
      case ELEVATOR:
        return Integer.parseInt(getProperty("elevator_system_port"));
      case FLOOR:
        return Integer.parseInt(getProperty("floor_system_port"));
      default:
        return Integer.parseInt(getProperty("scheduler_system_port"));
    }
  }
}
