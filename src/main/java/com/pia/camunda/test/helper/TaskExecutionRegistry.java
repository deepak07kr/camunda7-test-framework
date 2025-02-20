package com.pia.camunda.test.helper;

import com.pia.camunda.test.execution.TaskExecution;
import com.pia.camunda.test.model.EventType;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;

/**
 * TaskExecutionRegistry is a thread-safe, singleton registry designed to manage and retrieve
 * TaskExecution objects based on a task ID and specific EventType. It enables the dynamic
 * registration and sequential handling of task-specific behaviors used in workflow or BPM contexts.
 *
 * <p>This class uses a ConcurrentHashMap to store mappings of task IDs to event types, which then
 * map to queues of TaskExecution instances. The design ensures safe concurrent operations across
 * multiple threads.
 *
 * <p><strong>Design Pattern:</strong> Singleton This class applies the Singleton pattern, with the
 * static instance accessible via the {@code getInstance()} method. This ensures consistent access
 * to a single registry instance throughout the application, improving resource management and
 * enforcing a centralized repository.
 *
 * <p>Usage Example:
 *
 * <pre>{@code
 * TaskExecutionRegistry registry = TaskExecutionRegistry.getInstance();
 *
 * // Registering a TaskExecution instance
 * registry.register("taskId1", EventType.START, taskExecutionInstance);
 *
 * // Polling a TaskExecution object
 * TaskExecution execution = registry.poll("taskId1", EventType.START);
 * if (execution != null) {
 *     execution.execute(executionContext);
 * }
 *
 * // Clearing the registry
 * registry.clear();
 * }</pre>
 *
 * <p>This class is beneficial in scenarios involving dynamic task execution management, such as
 * workflow engines or event handlers.
 *
 * <p><strong>Methods:</strong>
 *
 * <ul>
 *   <li><strong>register(String taskId, EventType eventType, TaskExecution
 *       taskExpectation):</strong> Registers a TaskExecution object under the specified task ID and
 *       event type. If no mapping exists, it initializes the necessary data structures.
 *       <ul>
 *         <li><strong>Parameters:</strong>
 *             <ul>
 *               <li>{@code taskId} - The unique identifier of the task.
 *               <li>{@code eventType} - The type of event (START or END).
 *               <li>{@code taskExpectation} - The TaskExecution object containing task-specific
 *                   logic.
 *             </ul>
 *         <li><strong>Return Type:</strong> {@code void}
 *       </ul>
 *   <li><strong>poll(String taskId, EventType eventType):</strong> Retrieves and removes the head
 *       of the queue for a specific task ID and event type. Returns {@code null} if no mapping
 *       exists or the queue is empty.
 *       <ul>
 *         <li><strong>Parameters:</strong>
 *             <ul>
 *               <li>{@code taskId} - The unique identifier of the task.
 *               <li>{@code eventType} - The type of event (START or END).
 *             </ul>
 *         <li><strong>Return Type:</strong> {@code TaskExecution}
 *         <li><strong>Exceptions:</strong> None
 *       </ul>
 *   <li><strong>clear():</strong> Clears all registered tasks and their associated event-task
 *       mappings.
 *       <ul>
 *         <li><strong>Return Type:</strong> {@code void}
 *         <li><strong>Exceptions:</strong> None
 *       </ul>
 * </ul>
 *
 * @see EventType
 * @see TaskExecution
 * @see ConcurrentLinkedQueue
 * @author: Yusuf BOZKURT
 */
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

  public void clear() {
    registryMap.clear();
  }
}
