package sysc.g1.network;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sysc.g1.entity.EntityType;
import sysc.g1.events.ElevatorInfo;
import sysc.g1.events.Event;
import sysc.g1.events.EventInfo;
import sysc.g1.events.EventType;
import sysc.g1.utils.Direction;

import java.io.IOException;
import java.net.*;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class NetworkServiceTest {

  private final int RECEIVE_PORT = 8809;
  private final int SEND_PORT = 8810;
  private static NetworkService networkService;

  @BeforeClass
  public static void setUp() throws Exception {
    networkService = new NetworkService(null, 8809, InetAddress.getLocalHost());
  }


  @Test
  public void createPacket_test() throws IOException, ClassNotFoundException {
    ElevatorInfo sendInfo = new ElevatorInfo(1, EventType.ELEVATOR_ACTIVATE, 0, Direction.NONE);
    Event sendEvent = new Event(LocalTime.now(), EntityType.ELEVATOR, sendInfo, "some-id", "elevator");

    DatagramPacket packet = networkService.createPacket(sendEvent, InetAddress.getLocalHost(), SEND_PORT);

    assertEquals(SEND_PORT, packet.getPort());
    assertEquals(InetAddress.getLocalHost(), packet.getAddress());

    Event e = networkService.parsePacket(packet);
    EventInfo info = e.getInfo();

    assertEquals("some-id", e.getSourceId());
    assertEquals("elevator", e.getTargetId());

    assertNotSame(EventType.ELEVATOR_PENDING, info.getType());
    assertEquals(EventType.ELEVATOR_ACTIVATE, info.getType());
  }

  @Test
  public void send_test() throws  IOException {
    DatagramSocket testReceiveSocket = null;

    try {
      testReceiveSocket = new DatagramSocket(SEND_PORT);
    } catch (SocketException e) {
      e.printStackTrace();
    }

    ElevatorInfo sendInfo = new ElevatorInfo(4, EventType.ELEVATOR_ACTIVATE, 0, Direction.NONE);
    Event sendEvent = new Event(LocalTime.now(), EntityType.ELEVATOR, sendInfo, "some-id", "elevator");

    networkService.send(sendEvent, InetAddress.getLocalHost(), SEND_PORT);


    byte[] data = new byte[7000];
    DatagramPacket receivedPacket = new DatagramPacket(data, data.length);

    try {
      testReceiveSocket.receive(receivedPacket);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Event receivedEvent = null;
    try {
      receivedEvent = networkService.parsePacket(receivedPacket);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    EventInfo info = receivedEvent.getInfo();

    assertEquals("some-id", receivedEvent.getSourceId());
    assertEquals("elevator", receivedEvent.getTargetId());

    assertNotSame(EventType.ELEVATOR_PENDING, info.getType());
    assertEquals(EventType.ELEVATOR_ACTIVATE, info.getType());

    testReceiveSocket.close();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    networkService.close();
  }
}