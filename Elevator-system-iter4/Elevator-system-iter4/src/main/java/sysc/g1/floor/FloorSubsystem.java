package sysc.g1.floor;

import static sysc.g1.network.SubsystemPorts.FLOOR_PORT;

import sysc.g1.entity.Observable;
import sysc.g1.events.Event;
import sysc.g1.network.NetworkService;
import sysc.g1.network.SubsystemPorts;
import sysc.g1.utils.ConfigParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class FloorSubsystem implements Observable {

  private List<Floor> floors;
  private int numberOfElevators;

  private NetworkService networkService;
  
  private InetAddress address;

  private final int totalFloors = 10;

  /**
   * Constructor
   */
  public FloorSubsystem(int numFloors, int numberOfElevators, InetAddress address, int port) {
    this.numberOfElevators = numberOfElevators;
    initFloors(numFloors);
    this.address = address;
    this.networkService = new NetworkService(this, port, address);
  }

  private void initFloors(int numFloors) {
    this.floors = new ArrayList<>();
    for (int i=1; i<=numFloors; i++) {
      floors.add(new Floor(i, this, numberOfElevators));
    }
  }

  /**
   * use networkService to send ev to Scheduler
   */
  public void floorToScheduler(Event ev) {
    networkService.send(ev, SubsystemPorts.SCHEDULER_IP, SubsystemPorts.SCHEDULER_PORT);
  }

  public Floor getFloor(int level) {
    return floors.get(level - 1);
  }

  public void start() {
    floors.forEach(floor -> {
      Thread t = new Thread(floor);
      t.start();
    });
    networkService.start();
  }

  @Override
  public void update(Event event) {
    int floor = Floor.getLevelFromTarget(event.getTargetId());
    if (floor > 0) {
      floors.get(floor - 1).receiveEvent(event);
    } else {
      floors.forEach(f -> f.receiveEvent(event));
    }
  }

  public void close() {
    networkService.close();
  }

  public static void main(String[] args) throws UnknownHostException {
    ConfigParser config = new ConfigParser();
    System.out.println("Floor Subsystem starts");
    new FloorSubsystem(
        config.getIntegerProperty("number_of_floors"),
        config.getIntegerProperty("number_of_elevators"),
        SubsystemPorts.FLOOR_IP, FLOOR_PORT).start();
  }
}
