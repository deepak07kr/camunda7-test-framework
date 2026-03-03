package org.opentmf.camunda.test.util;

import org.opentmf.camunda.test.builder.ReceiveTaskExpectationBuilder;
import org.opentmf.camunda.test.builder.ReceiveTaskExpectationBuilderImpl;
import org.opentmf.camunda.test.builder.TaskExpectationBuilder;
import org.opentmf.camunda.test.builder.TaskExpectationBuilderImpl;
import org.opentmf.camunda.test.helper.ReceiveTaskManager;
import org.opentmf.camunda.test.helper.TaskExecutionRegistry;

/**
 * Utility class that simplifies the registration of task execution listeners for Camunda BPM tasks.
 * It provides entry points to configure and register listeners for both user tasks and
 * message-waiting elements using a fluent builder pattern. This class enables streamlined task
 * execution customization within Camunda processes, aiding in managing task-specific behavior
 * efficiently.
 *
 * <p>Since this class only provides static utility methods, it is not meant to be instantiated. All
 * interactions should occur through its static methods.
 *
 * <p>Design Pattern: Builder Pattern - Makes use of builder classes such as
 * `TaskExpectationBuilder` and `ReceiveTaskExpectationBuilder`. - Ensures fluent APIs for
 * constructing task expectations step-by-step before registration.
 *
 * <p><b>Usage Example</b>:
 *
 * <pre>
 * // Register a task execution listener for user tasks
 * CamundaExpectationUtil.registerTaskExecutionListener()
 *     .withTaskId("userTaskId")
 *     .withEventType(EventType.COMPLETE)
 *     .withVariableMap(Map.of("key", "value"))
 *     .create();
 *
 * // Register a message catch execution listener (supports Receive Task,
 * // Message Intermediate Catch Event, and Boundary Message Event)
 * CamundaExpectationUtil.registerMessageCatchExecutionListener()
 *     .withTaskId("messageCatchElementId")
 *     .withCorrelationMessage("correlationMessage")
 *     .withVariableMap(Map.of())
 *     .create();
 * </pre>
 *
 * @see TaskExpectationBuilder
 * @see ReceiveTaskExpectationBuilder
 * @see VariableUtil
 * @see TaskExecutionRegistry
 * @see ReceiveTaskManager
 *     <p><b>Author:</b> Yusuf BOZKURT
 */
public class CamundaExpectationUtil {
  private CamundaExpectationUtil() {}

  /**
   * Registers a task execution listener for user tasks in Camunda BPM. This method returns a
   * `TaskExpectationBuilder` instance to configure the task listener with specific attributes.
   *
   * @return a new `TaskExpectationBuilder` instance to configure the task listener
   */
  public static TaskExpectationBuilder registerTaskExecutionListener() {
    return new TaskExpectationBuilderImpl();
  }

  /**
   * Registers an execution listener for message-waiting BPMN elements in Camunda BPM. This method
   * returns a `ReceiveTaskExpectationBuilder` instance to configure the listener with specific
   * attributes.
   *
   * <p>Supported BPMN element types:
   *
   * <ul>
   *   <li>Receive Task
   *   <li>Message Intermediate Catch Event
   *   <li>Boundary Message Event
   * </ul>
   *
   * @return a new `ReceiveTaskExpectationBuilder` instance to configure the listener
   */
  public static ReceiveTaskExpectationBuilder registerMessageCatchExecutionListener() {
    return new ReceiveTaskExpectationBuilderImpl();
  }

}
