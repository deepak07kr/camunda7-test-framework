package com.pia.camunda.test.helper;

import com.pia.camunda.test.model.EventType;
import com.pia.camunda.test.execution.TaskExecution;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;

public class TaskExecutionRegistry {

  @Getter private static final TaskExecutionRegistry instance = new TaskExecutionRegistry();

  private final Map<String, Map<EventType, Queue<TaskExecution>>> registryMap =
      new ConcurrentHashMap<>();

  private TaskExecutionRegistry() {}

  public void register(String taskId, TaskExecution taskExpectation) {
    register(taskId, EventType.START, taskExpectation);
  }

  public void register(String taskId, EventType eventType, TaskExecution taskExpectation) {
    registryMap
        .computeIfAbsent(taskId, k -> new ConcurrentHashMap<>())
        .computeIfAbsent(eventType, e -> new ConcurrentLinkedQueue<>())
        .add(taskExpectation);
  }


  public TaskExecution poll(String taskId, String eventName) {
    var eventType = parseEventType(eventName);
    return poll(taskId, eventType);
  }

  public TaskExecution poll(String taskId, EventType eventType) {
    Map<EventType, Queue<TaskExecution>> eventMap = registryMap.get(taskId);
    if (eventMap == null) {
      return null;
    }
    Queue<TaskExecution> queue = eventMap.get(eventType);
    if (queue == null || queue.isEmpty()) {
      return null;
    }
    return queue.poll();
  }

  private EventType parseEventType(String eventName) {
    if ("end".equalsIgnoreCase(eventName)) {
      return EventType.END;
    }
    return EventType.START;
  }
}
