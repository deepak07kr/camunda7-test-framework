package org.opentmf.camunda.test.builder;

import org.opentmf.camunda.test.execution.CustomTaskExecution;
import org.opentmf.camunda.test.helper.TaskExecutionRegistry;
import org.opentmf.camunda.test.model.EventType;

/**
 * The `TaskExpectationBuilder` interface is a specialized extension of the {@link
 * BaseTaskExpectationBuilder} interface, designed to simplify and streamline the configuration of
 * task expectations with additional flexibility for handling event-based behaviors. Leveraging the
 * **Builder Pattern**, this interface provides a fluent API to construct and configure tasks before
 * their execution, making it ideal for scenarios that involve event-driven operations such as
 * registering tasks based on specific `EventType`s.
 *
 * <h2>Key Functionality</h2>
 *
 * This builder adds the `withEventType(EventType)` method to configure tasks tied to particular
 * event lifecycles. It operates seamlessly alongside other inherited configuration methods like
 * `withTaskId`, `withVariableMap`, and `withRunnable`, culminating in a call to `create()` to
 * finalize the task registration.
 *
 * <h2>Core Methods</h2>
 *
 * <ul>
 *   <li>{@link #withEventType(EventType)}: Specifies the type of event (e.g., START or END) that
 *       the task is targeting.
 *       <ul>
 *         <li><b>Parameter:</b> `eventType` - Enum value representing the event type (from {@link
 *             EventType}).
 *         <li><b>Return Type:</b> A fluent reference to the current builder instance
 *             (`TaskExpectationBuilder`).
 *         <li><b>Usage:</b> This is particularly useful when tasks need to be triggered or
 *             associated with specific events or phases in a process lifecycle.
 *       </ul>
 * </ul>
 *
 * <h2>How the Builder Pattern is Applied</h2>
 *
 * The **Builder Pattern** is implemented here to allow step-by-step configuration of a task's
 * expectations. This design encourages clean and readable code, particularly when multiple
 * properties need to be configured. By chaining method calls, developers can fluently define a
 * task's behavior without cluttering the codebase with constructor overloads or additional helper
 * methods. Once all configurations are complete, the `create()` method is called to finalize and
 * register the task.
 *
 * <h2>Example Usage</h2>
 *
 * Below is an example that demonstrates how to use `TaskExpectationBuilder`:
 *
 * <pre>{@code
 * TaskExpectationBuilder builder = new TaskExpectationBuilderImpl()
 *     .withTaskId("task456")
 *     .withVariableMap(Map.of("key1", "value1"))
 *     .withRunnable(() -> System.out.println("Task Completed"))
 *     .withEventType(EventType.START);
 * builder.create();
 * }</pre>
 *
 * The above example demonstrates configuring a task with a unique ID, variables, custom logic, and
 * the START event type before finalizing with `create()` to register it.
 *
 * <h2>Design Benefits</h2>
 *
 * <ul>
 *   <li><b>Modularity:</b> Each method focuses on configuring a single attribute, ensuring
 *       modularity.
 *   <li><b>Fluency:</b> The chainable API enables intuitive and expressive task configuration.
 *   <li><b>Reusability:</b> Generic base interface allows extensible implementations specific to
 *       different use cases (e.g., receive tasks).
 * </ul>
 *
 * <h2>Extensibility</h2>
 *
 * Implementors of this interface should focus on providing concrete logic for how tasks and related
 * events are registered and executed. For instance, `TaskExpectationBuilderImpl` adds custom
 * behavior for managing event-based task registration within the {@link TaskExecutionRegistry}.
 *
 * @see BaseTaskExpectationBuilder
 * @see AbstractTaskExpectationBuilder
 * @see TaskExecutionRegistry
 * @see EventType
 * @see CustomTaskExecution
 * @see ReceiveTaskExpectationBuilder
 * @see TaskExpectationBuilderImpl
 * @see Runnable
 * @author Yusuf BOZKURT
 */
public interface TaskExpectationBuilder extends BaseTaskExpectationBuilder<TaskExpectationBuilder> {

  TaskExpectationBuilder withEventType(EventType eventType);
}
