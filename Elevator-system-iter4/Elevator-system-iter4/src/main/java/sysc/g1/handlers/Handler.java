package sysc.g1.handlers;

import sysc.g1.elevator.Elevator;
import sysc.g1.events.Event;
import sysc.g1.exceptions.FloorMismatchedStateException;
import sysc.g1.floor.Floor;
import sysc.g1.scheduler.SchedulerSubsystem;

/**
 * The base class to handle all the incoming events.
 * 
 * For each event type, there will be a matching handler which handler that event. Each handler
 * contains 3 methods:
 *
 * {@code handleOnFloor} Represent how the floor should handle this event
 * {@code handleOnScheduler} Represent how the Scheduler should handle this event
 * {@code handleOnElevator} Represent how the elevator should handle this event
 *
 * Each handler should contains 3 total phases during execution:
 *
 * - Parse the event to get all the necessary information (which floor, which elevator, up or down)
 * - Analyze the current state together with the event to perform correct actions
 *   - Note: the action is bad if it does too much or too little
 * - The action will transition the subsystem to a new state. A new event need to be construct to
 *   notify other subsystems about this change
 */
public abstract class Handler {

  /**
   * Represent how the floor will handle the incoming event. Should only be executed from the
   * floor subsystem
   *
   * @param event the incoming event
   * @param floor the floor object to handle this event
   */
  public abstract void handleOnFloor(Event event, Floor floor);

  /**
   * Represent how the scheduler will handle the incoming event. Should only be executed from the
   * scheduler subsystem
   *
   * @param event the incoming event
   * @param schedulerSubsystem the schedule subsystem which will handle this event
   */
  public abstract void handleOnScheduler(Event event, SchedulerSubsystem schedulerSubsystem);

  /**
   * Represent how the elevator will handle the incoming event. Should only be executed from the
   * elevator subsystem
   *
   * @param event the incoming event
   * @param elevator the elevator thread which will handle this event
   */
  public abstract void handleOnElevator(Event event, Elevator elevator);
}
