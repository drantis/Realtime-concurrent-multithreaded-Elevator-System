package sysc.g1.floor;

import sysc.g1.utils.Direction;
import sysc.g1.states.LampState;

/**
 * The floor lamp
 */
public class FloorLamp {
    private Direction direction;
    private LampState state;
    private int floor;

    public FloorLamp(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
        this.state = LampState.OFF;
    }

    /**
     * Switch lamp to on/off
     */
    public void switchLamp(LampState state) {
        this.state = state;
    }

    public Direction getDirection() {
        return direction;
    }

    public LampState getState() {
        return state;
    }

    public int getFloor() {
        return floor;
    }

    /**
     * Display the status of the lamp
     */
    public String display(){
        return String.format("%s Lamp of floor %d: %s", direction, floor, state);
    }
}
