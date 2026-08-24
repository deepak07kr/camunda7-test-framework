package org.opentmf.camunda.test.builder;

import org.opentmf.camunda.test.execution.CustomTaskExecution;
import org.opentmf.camunda.test.execution.SimulatedTaskFailure;
import org.opentmf.camunda.test.helper.TaskExecutionRegistry;
import org.opentmf.camunda.test.model.EventType;
import java.time.Duration;

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

  /**
   * Makes the expectation FAIL the activity instead of completing it: when the listener fires, a
   * {@link SimulatedTaskFailure} is thrown with the given message. Variables and consumers
   * registered on the same expectation are applied first, so a test can both leave evidence and
   * blow up.
   *
   * <p>An incident only appears when the scripted failures cover EVERY attempt of an asynchronous
   * continuation. Expectations are one-shot (consumed by the attempt that triggers them), so under
   * the engine's default retry cycle a single {@code withFailure} means "fails once, completes on
   * retry" — no incident. Either give the activity {@code asyncBefore} with a retry cycle the
   * failures exhaust (e.g. {@code R1/PT0S}), or register {@code withCount(n)} failures matching
   * the configured retries. On a NON-async activity there is no job to retry at all: the exception
   * propagates straight to the caller ({@code startProcessInstance(...)}, a message correlation)
   * instead of creating an incident. Register a plain expectation after a failing one for "fails,
   * then succeeds".
   */
  TaskExpectationBuilder withFailure(String message);

  /**
   * Makes the expectation raise a BPMN error with the given code — the declarative way to drive an
   * error boundary event in the model under test.
   */
  TaskExpectationBuilder withBpmnError(String errorCode);

  /**
   * Delays the expectation before anything else it does — the declarative slow task. Use to hold a
   * process at an activity long enough for the test to observe an in-flight state. The delay runs
   * in whatever thread executes the activity: on a NON-async activity that is the caller's own
   * thread — {@code startProcessInstance(...)} itself blocks — so observing an in-flight state
   * from the test requires an asynchronous continuation on the activity.
   */
  TaskExpectationBuilder withDelay(Duration delay);
}
