package sysc.g1.events;

import sysc.g1.entity.Entity;
import sysc.g1.entity.EntityType;

import java.io.Serializable;
import java.time.LocalTime;
import sysc.g1.handlers.Handler;

public class Event implements Serializable {
  private LocalTime time;
  private EntityType source;
  private EventInfo info;
  private String sourceId;
  private String targetId;
  private boolean urgent;

  public static Event getNullEvent() {
    return new Event();
  }

  public Event() {
    this.source = EntityType.NULL;
  }

  public Event(
      LocalTime time, EntityType source, EventInfo info, String sourceId, String targetId) {
    this.time = time;
    this.source = source;
    this.info = info;
    this.sourceId = sourceId;
    this.urgent = false;
    this.targetId = targetId;
  }

  public Event(
      LocalTime time, EntityType source, EventInfo info, String sourceId, String targetId, boolean urgent) {
    this.time = time;
    this.source = source;
    this.info = info;
    this.sourceId = sourceId;
    this.urgent = urgent;
    this.targetId = targetId;
  }

  public Event(Entity entity, LocalTime time, EventInfo info, String targetId) {
    this.source = entity.getEntityType();
    this.sourceId = entity.getId();
    this.time = time;
    this.info = info;
    this.urgent = false;
    this.targetId = targetId;
  }

  public Event(LocalTime time) {
    this.source = EntityType.NULL;
    this.sourceId = "NULL";
    this.time = time;
    this.urgent = false;
  }

  public Event(LocalTime time, boolean urgent) {
    this.source = EntityType.NULL;
    this.sourceId = "NULL";
    this.time = time;
    this.urgent = urgent;
  }

  public Event(Entity entity, EventInfo info) {
    this.source = entity.getEntityType();
    this.sourceId = entity.getId();
    this.time = LocalTime.now();
    this.info = info;
    this.urgent = false;
  }

  public Event(Event event) {
    this(event.time, event.source, event.info, event.sourceId, event.targetId);
  }

  public LocalTime getTimestamp() {
    return time;
  }

  public EntityType getSource() {
    return source;
  }

  public EventInfo getInfo() {
    return info;
  }

  public String getSourceId() {
    return sourceId;
  }

  public boolean isUrgent() {
    return urgent;
  }

  public String getTargetId() {
    return targetId;
  }

  public String toString() {
    return String.format("%s --> %s", sourceId, info);
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public Handler getHandler() {
    return this.info.getType().getHandler();
  }
}
