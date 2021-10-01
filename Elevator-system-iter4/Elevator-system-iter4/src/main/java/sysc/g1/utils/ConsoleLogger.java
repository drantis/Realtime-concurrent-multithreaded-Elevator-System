package sysc.g1.utils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import sysc.g1.events.Event;

public class ConsoleLogger {
  private static Logger log = Logger.getLogger(ConsoleLogger.class);

  private static ConsoleLogger instance;

  public ConsoleLogger() {
    BasicConfigurator.configure();
  }

  public static ConsoleLogger getInstance() {
    if (instance == null) {
      instance = new ConsoleLogger();
    }
    return instance;
  }

  public void log(String message) {
    System.out.println(message);
  }

  public void logEvent(Event event) {
    log.info(event);
  }
}
