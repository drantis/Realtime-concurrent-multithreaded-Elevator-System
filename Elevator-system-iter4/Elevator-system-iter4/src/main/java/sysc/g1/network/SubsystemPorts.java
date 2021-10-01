package sysc.g1.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SubsystemPorts {

  public static int FLOOR_PORT = 11000;
  public static int ELEVATOR_PORT = 22000;
  public static int SCHEDULER_PORT = 33000;

  public static InetAddress FLOOR_IP;

  static {
    try {
      FLOOR_IP = InetAddress.getLocalHost();
      //FLOOR_IP = InetAddress.getByName("172.17.139.169");
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public static InetAddress ELEVATOR_IP;

  static {
    try {
    	ELEVATOR_IP = InetAddress.getLocalHost();
      //ELEVATOR_IP = InetAddress.getByName("172.17.152.35");
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public static InetAddress SCHEDULER_IP;

  static {
    try {
//      SCHEDULER_IP = InetAddress.getByName("172.17.185.113");
      SCHEDULER_IP = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

}
