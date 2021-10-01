package sysc.g1.network;

import sysc.g1.entity.Observable;
import sysc.g1.events.Event;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class NetworkService implements Runnable {

  private DatagramSocket receivedSocket;
  private InetAddress address;
  private Observable observer;
  private int MAX_SIZE = 7000;
  private boolean isActive;


  public NetworkService(Observable observer, int receivedPort, InetAddress address) {
    this.observer = observer;

    try{
      this.receivedSocket = new DatagramSocket(receivedPort);
    } catch (SocketException e) {
      e.printStackTrace();
      System.exit(1);
    }

    this.address = address;
    this.isActive = true;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  /**
   * notify observer when received an incoming event
   */
  private void notifyObserver(Event event) {
    observer.update(event);
  }

  /**
   * create a DatagramPacket from a given Event and port
   * by converting Event to byte[], then from byte[] and port, convert it to DatagramPacket
   */
  public DatagramPacket createPacket(Event event, InetAddress address, int port) throws IOException {
    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(MAX_SIZE);
    final ObjectOutputStream oStream = new ObjectOutputStream(byteStream);
    oStream.writeObject(event);
    oStream.close();

    final byte[] data = byteStream.toByteArray();
    return new DatagramPacket(data, data.length, address, port);
  }

  /**
   * parse a DatagramPacket's data and convert it into Event object
   */
  public Event parsePacket(DatagramPacket packet) throws IOException, ClassNotFoundException {
    byte[] data = packet.getData();
    ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
    Event receivedEvent = (Event) iStream.readObject();
    iStream.close();
    return receivedEvent;
  }

  public void send(Event event, InetAddress address, int sendPort) {

    // construct a DatagramSocket for sending
    DatagramSocket sendSocket = null;
    try {
      sendSocket = new DatagramSocket();
    } catch (SocketException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // create a DatagramPacket to be sent
    DatagramPacket sendPacket = null;
    try {
      sendPacket = createPacket(event, address, sendPort);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Ship it !!
    try {
      sendSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

//    System.out.println("Sending");
    sendSocket.close();
  }

  /**
   * Polling, waiting to receive from teh RECEIVED_PORT
   */
  private synchronized void poll() throws IOException, ClassNotFoundException {
    //TODO: should we optimize the polling?
    while (isActive) {
      byte[] data = new byte[MAX_SIZE];
      DatagramPacket receivedPacket = new DatagramPacket(data, data.length);

      receivedSocket.receive(receivedPacket);

      Event event = parsePacket(receivedPacket);

      notifyObserver(event);
    }
  }


  @Override
  public void run() {
    try {
      poll();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    Thread pollThread = new Thread(this);
    pollThread.start();
  }

  public void close() {
    receivedSocket.close();
  }
}

