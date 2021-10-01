package sysc.g1.elevator;

import static sysc.g1.network.SubsystemPorts.ELEVATOR_PORT;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sysc.g1.entity.Observable;
import sysc.g1.events.*;

import sysc.g1.network.NetworkService;
import sysc.g1.network.SubsystemPorts;
import sysc.g1.scheduler.SchedulerSubsystem;
import sysc.g1.utils.ConfigParser;

public class ElevatorSubsystem implements Observable {

  private List<Elevator> elevators;
  private boolean[][] actorRequests;
  private int numberOfFloors;
  private int numberOfElevators;
  private InetAddress address;

  private NetworkService networkService;

  public ElevatorSubsystem(
      int numberOfFloors, int numberOfElevators, InetAddress address, int port) {
    this.elevators = new ArrayList<>();
    this.numberOfFloors = numberOfFloors;
    this.numberOfElevators = numberOfElevators;
    for (int i = 1; i <= numberOfElevators; i++) {
      elevators.add(new Elevator(i, this, numberOfFloors));
    }

    actorRequests = new boolean[numberOfFloors + 1][numberOfFloors + 1];
    this.address = address;
    this.networkService = new NetworkService(this, port, address);
  }

  public ElevatorSubsystem(
      int numberOfFloors,
      int numberOfElevators,
      int[] initialLocations,
      InetAddress address,
      int port) {
    this(numberOfFloors, numberOfElevators, address, port);
    for (int i = 0; i < numberOfElevators; i++) {
      this.elevators.get(i).setCurrFloor(initialLocations[i]);
    }
  }

  public void setNetworkService() {
    this.networkService = networkService;
  }

  public Elevator getElevator(int id) {
    return elevators.get(id - 1);
  }

  public void setActorRequests(int from, int to) {
    actorRequests[from][to] = true;
  }

  /** use NetworkService to send event to Scheduler */
  public void elevatorToScheduler(Event event) {
    networkService.send(event, this.address, SubsystemPorts.SCHEDULER_PORT);
  }

  public synchronized List<Event> executeActorRequests(
      int elevatorId, LocalTime timestamp, int currFloor) {
    List<Event> eventList = new ArrayList<>();
    for (int i = 1; i <= numberOfFloors; i++) {
      if (actorRequests[currFloor][i]) {
        eventList.add(elevators.get(elevatorId - 1).buttonPressed(timestamp, i));
        actorRequests[currFloor][i] = false;
      }
    }
    return eventList;
  }

  @Override
  public void update(Event ev) {
    int elevatorIndex = Elevator.getElevatorFromTarget(ev.getTargetId());
    if (elevatorIndex > 0) {
      elevators.get(elevatorIndex - 1).receiveEvent(ev);
    } else {
      elevators.forEach(e -> e.receiveEvent(ev));
    }
}

  public void start() {
    elevators.forEach(
        elevator -> {
          Thread t = new Thread(elevator);
          t.start();
        });
    networkService.start();
  }

  public static void main(String[] args) throws UnknownHostException {
    ConfigParser config = new ConfigParser();
    System.out.println("Elevators start");
    new ElevatorSubsystem(
            config.getIntegerProperty("number_of_floors"),
            config.getIntegerProperty("number_of_elevators"),
            config.getArrayProperty("initial_locations"),
            SubsystemPorts.ELEVATOR_IP,
            ELEVATOR_PORT)
        .start();
  }

  public void close() {
    networkService.close();
  }
}
