package sysc.g1.elevator;

import sysc.g1.entity.EntityType;
import sysc.g1.events.*;

import java.time.LocalTime;
import sysc.g1.utils.Direction;

public class ElevatorButton {
    private int level;
    private String elevatorId;
    private int elevatorIndex;

    public ElevatorButton(int elevatorIndex, String elevatorId, int level) {
        this.level = level;
        this.elevatorId = elevatorId;
        this.elevatorIndex = elevatorIndex;
    }

    public Event press(LocalTime time, int currFloor){
      EventInfo req = new ElevatorRequestInfo(elevatorIndex, currFloor, level, Direction.NONE);
      return new Event(time, EntityType.ELEVATOR, req, elevatorId, "scheduler");
    }
}
