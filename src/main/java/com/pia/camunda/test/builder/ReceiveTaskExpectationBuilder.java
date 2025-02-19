package com.pia.camunda.test.builder;

import com.pia.camunda.test.helper.ReceiveTaskManager;
import com.pia.camunda.test.helper.TaskExecutionRegistry;
import java.util.Map;

/**
 * The `ReceiveTaskExpectationBuilder` interface extends the functionality of the
 * `BaseTaskExpectationBuilder` by introducing a specialized method for configuring correlation
 * messages. It is specifically designed for building and registering expectations related to
 * receive tasks in a workflow or process, based on the **Builder Pattern**.
 *
 * <h2>Purpose</h2>
 *
 * This interface enables developers to specify correlation messages associated with a receive task,
 * in addition to the generic configurations offered by `BaseTaskExpectationBuilder`. It allows the
 * fluent creation of robust and modular task expectations.
 *
 * <h2>Key Features</h2>
 *
 * - Provides a method to set a correlation message for receive tasks. - Ensures fluent and
 * easy-to-read task configuration using method chaining. - Integrates with the existing task
 * expectation framework, adhering to the builder pattern.
 *
 * <h3>Available Methods</h3>
 *
 * <ul>
 *   <li>{@link #withCorrelationMessage(String)}:
 *       <ul>
 *         <li><b>Description:</b> Adds a correlation message that can be used to match specific
 *             messages in the process.
 *         <li><b>Parameter:</b> `msg` - A `String` representing the correlation message.
 *         <li><b>Return Type:</b> Fluent reference to the `ReceiveTaskExpectationBuilder` instance.
 *         <li><b>Example Usage:</b> `builder.withCorrelationMessage("OrderCreated").create();`
 *       </ul>
 *   <li>Inherited methods from {@link BaseTaskExpectationBuilder}:
 *       <ul>
 *         <li>{@link BaseTaskExpectationBuilder#withTaskId(String)}: Assigns a unique task ID.
 *         <li>{@link BaseTaskExpectationBuilder#withVariableMap(Map)} : Sets a map of variables to
 *             use during task execution.
 *         <li>{@link BaseTaskExpectationBuilder#withRunnable(Runnable)}: Defines custom logic to
 *             run during task execution.
 *         <li>{@link BaseTaskExpectationBuilder#create()}: Registers and finalizes the task
 *             expectation.
 *       </ul>
 * </ul>
 *
 * <h2>Design Pattern</h2>
 *
 * The `ReceiveTaskExpectationBuilder` employs the **Builder Pattern**, enabling the incremental and
 * readable configuration of task expectations. This approach ensures immutability of task-specific
 * properties after creation and simplifies complex object construction.
 *
 * <h2>Usage Example</h2>
 *
 * Here's an example of how you can use `ReceiveTaskExpectationBuilder`:
 *
 * <pre>{@code
 * ReceiveTaskExpectationBuilder builder = new ReceiveTaskExpectationBuilderImpl()
 *     .withTaskId("receiveTask1")
 *     .withVariableMap(Map.of("userId", 123))
 *     .withRunnable(() -> System.out.println("Task Executed"))
 *     .withCorrelationMessage("Message123");
 * builder.create();
 * }</pre>
 *
 * This example illustrates how to configure and finalize a receive task with an associated
 * correlation message and custom logic.
 *
 * @see BaseTaskExpectationBuilder
 * @see ReceiveTaskExpectationBuilderImpl
 * @see TaskExecutionRegistry
 * @see ReceiveTaskManager
 * @author : Yusuf BOZKURT
 */
public interface ReceiveTaskExpectationBuilder
    extends BaseTaskExpectationBuilder<ReceiveTaskExpectationBuilder> {

  ReceiveTaskExpectationBuilder withCorrelationMessage(String msg);
}
