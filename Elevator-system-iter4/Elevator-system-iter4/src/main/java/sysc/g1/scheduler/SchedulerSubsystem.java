package sysc.g1.scheduler;

import static sysc.g1.network.SubsystemPorts.SCHEDULER_IP;
import static sysc.g1.network.SubsystemPorts.SCHEDULER_PORT;

import org.mockito.internal.creation.SuspendMethod;
import sysc.g1.entity.Entity;
import sysc.g1.entity.EntityType;
import sysc.g1.entity.Observable;
import sysc.g1.events.Event;
import sysc.g1.network.NetworkService;
import sysc.g1.network.SubsystemPorts;
import sysc.g1.utils.ConfigParser;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SchedulerSubsystem extends Entity implements Observable {

  public static final String TARGET_ID = "scheduler";
  private ControllerBoard controllerBoard;
  private int numberOfFloors;
  private int numberOfElevators;
  private NetworkService networkService;

  public SchedulerSubsystem(int numOfFloors, int numOfEle, InetAddress address, int port) {
    super(EntityType.SCHEDULER, TARGET_ID);
    this.controllerBoard = new ControllerBoard(numOfFloors, numOfEle);
    this.numberOfFloors = numOfFloors;
    this.numberOfElevators = numOfEle;
    this.networkService = new NetworkService(this, port, address);
  }

  public ControllerBoard getControllerBoard() {
    return controllerBoard;
  }

  public void notifyFloor(Event event) {
    display(event.getTimestamp(), String.format("Notify %s: %s", event.getTargetId(), event));
    networkService.send(event, SubsystemPorts.FLOOR_IP, SubsystemPorts.FLOOR_PORT);
  }

  public void notifyElevator(Event event) {
    display(event.getTimestamp(), String.format("Notify %s: %s", event.getTargetId(), event));
    networkService.send(event, SubsystemPorts.ELEVATOR_IP, SubsystemPorts.ELEVATOR_PORT);
  }

  public int getNumberOfFloors() {
    return numberOfFloors;
  }

  @Override
  public void processEvent(Event event) {
//    System.out.println(this.pendingEvents());
//    System.out.println(event);
    event.getHandler().handleOnScheduler(event, this);
  }

  @Override
  public void update(Event event) {
    this.receiveEvent(event);
  }

  public void start() {
    new Thread(this).start();
    networkService.start();
  }

  public static void main(String[] args) throws UnknownHostException {
    ConfigParser config = new ConfigParser();

    System.out.println("Scheduler starts");
    new SchedulerSubsystem(
        config.getIntegerProperty("number_of_floors"),
        config.getIntegerProperty("number_of_elevators"),
        SCHEDULER_IP,
        SCHEDULER_PORT).start();
  }

  public void close() {
    networkService.close();
  }
}
