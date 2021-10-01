package sysc.g1.exceptions;

import java.util.Set;
import sysc.g1.states.FloorState;

/**
 * Throws this when a floor event happens in an invalid state
 */
public class FloorMismatchedStateException extends Exception {

  public static FloorMismatchedStateException create(String message, Set<FloorState> states) {
    return new FloorMismatchedStateException(buildMessage(message, states));
  }

  public static FloorMismatchedStateException create(String message) {
    return new FloorMismatchedStateException(message);
  }

  public FloorMismatchedStateException(String message) {
    super(message);
  }

  private static String buildMessage(String message, Set<FloorState> states) {
    StringBuilder errorString = new StringBuilder(": ");
    for (FloorState state : states) {
      errorString.append(state);
    }
    return message + errorString.toString();
  }
}
