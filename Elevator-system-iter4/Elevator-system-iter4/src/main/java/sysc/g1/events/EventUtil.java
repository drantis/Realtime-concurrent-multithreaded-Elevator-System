package sysc.g1.events;

import java.io.File;
import sysc.g1.elevator.Elevator;
import sysc.g1.floor.Floor;
import sysc.g1.utils.Direction;
import sysc.g1.entity.EntityType;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;

/**
 * This class Represents the list of Events that will be parsed from a txt file and used as
 * instructions
 */
public class EventUtil {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  private String fileName = "extreme.txt";

  /**
   * Read events from selected file
   *
   * @return a list of events
   */
  public List<Event> readEvents() {
    List<Event> events = null;

    File file = this.filePicker();

    if (file != null) {
      events = readEventsFromFile(file);
    }
    return events;
  }

  /**
   * This method read event from a file and parse to a list of event
   *
   * @param file: the File
   * @return the list of parsed events from the file
   */
  private List<Event> readEventsFromFile(File file) {
    List<Event> events = new ArrayList<>();
    try {
      // Attempting to open and read the file
      events = Files.lines(file.toPath()).map(this::parse).collect(Collectors.toList());
    } catch (IOException e) { // Catching error
      e.printStackTrace();
      System.exit(1);
    }
    return events;
  }

  /**
   * Transform event string into Event object
   *
   * @param eventString the event string
   * @return the Event object that matches
   */
  public Event parse(String eventString) {
    String[] data = eventString.split(" "); // Spiting the information by spaces
    // Read the original floor, destination floor and direction
    if (data[1].equals("error")) {
      return parseErrorEvent(data);
    }
    int fromFloor = Integer.parseInt(data[1]);
    int toFloor = Integer.parseInt(data[3]);
    Direction direction = data[2].toLowerCase().equals("up") ? Direction.UP : Direction.DOWN;
    // Put them info event info object
    EventInfo info =
        new ElevatorRequestInfo(0, fromFloor, toFloor, direction, EventType.BUTTON_PRESS);
    // Change the time string into LocalTime object
    LocalTime timestamp = LocalTime.parse(data[0], DATE_FORMAT);

    return new Event(timestamp, EntityType.ACTOR, info, "actor", Floor.getTargetId(fromFloor));
  }

  public Event parseErrorEvent(String[] data) {
    LocalTime timestamp = LocalTime.parse(data[0], DATE_FORMAT);
    int elevator_id = Integer.parseInt(data[3]);
    String error = data[4];
    EventInfo info;
    if (data.length == 8) {
      int duration = Integer.parseInt(data[6]);
      info = new ElevatorErrorInfo(elevator_id, error, duration);
    } else {
      info = new ElevatorErrorInfo(elevator_id, error);
    }
    return new Event(timestamp, EntityType.ACTOR, info, "actor", Elevator.getTargetId(elevator_id));
  }

  private File filePicker() {

    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle("Choose Event File");
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile();
    } else {
      System.out.println("No Selection ");
      return null;
    }
  }

  public static void main(String[] args) {

    EventUtil eventUtil = new EventUtil();
    eventUtil.filePicker();
    List<Event> events = eventUtil.readEvents();

    System.out.println(events.get(0).getTimestamp());
    System.out.println(events.get(0).getInfo());
    events.forEach(System.out::println);

    System.out.println(eventUtil.parse("14:05:15.800 2 Up 4"));
  }
}
