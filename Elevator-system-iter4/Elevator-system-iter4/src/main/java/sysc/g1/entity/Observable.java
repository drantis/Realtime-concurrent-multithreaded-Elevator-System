package sysc.g1.entity;

import sysc.g1.events.Event;

public interface Observable {
  void update(Event event);
}
