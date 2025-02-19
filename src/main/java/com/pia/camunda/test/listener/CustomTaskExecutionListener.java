package com.pia.camunda.test.listener;

import com.pia.camunda.test.execution.TaskExecution;
import com.pia.camunda.test.helper.TaskExecutionRegistry;
import java.util.Objects;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 * The CustomTaskExecutionListener class is a listener implementation designed to act as a hook
 * during the execution of tasks in a BPMN (Business Process Model and Notation) process. This
 * listener is registered to specific BPMN tasks and listens for execution-related events (e.g.,
 * start and end events) triggered during the lifecycle of these tasks.
 *
 * <p>Functionality: Upon being notified of an event, this class retrieves a matching {@link
 * TaskExecution} object from the {@link TaskExecutionRegistry}, if available, and proceeds to
 * execute defined custom logic by invoking the `execute` method of the retrieved task.
 *
 * <p>Usage: The CustomTaskExecutionListener is typically added at runtime during the BPMN parsing
 * phase using Camunda's BPMN ParseListeners, as demonstrated in {@link BpmnTaskParseListener}.
 * Developers can register tasks and corresponding event-specific behaviors through the {@link
 * TaskExecutionRegistry}.
 *
 * <p>Example: Within a BPMN setup: - Register tasks and expectations: {@code
 * TaskExecutionRegistry.getInstance().register(taskId, expectation);} - Attach
 * CustomTaskExecutionListener to a BPMN activity for relevant events: {@code
 * activity.addListener(ExecutionListener.EVENTNAME_START, new CustomTaskExecutionListener());}
 *
 * @see ExecutionListener
 * @see DelegateExecution
 * @see TaskExecutionRegistry
 * @see TaskExecution
 * @author Yusuf Bozkurt
 */
@Getter
public class CustomTaskExecutionListener implements ExecutionListener {

  /**
   * This method is called when an event is triggered during the execution of a BPMN task. It
   * retrieves a {@link TaskExecution} object from the {@link TaskExecutionRegistry} based on the
   * current activity ID and event name. If a matching task is found, the task's `execute` method is
   * invoked to execute the defined custom logic.
   *
   * @param execution The DelegateExecution object representing the current execution context.
   */
  @Override
  public void notify(DelegateExecution execution) {
    var taskExpectation =
        TaskExecutionRegistry.getInstance()
            .poll(execution.getCurrentActivityId(), execution.getEventName());
    if (Objects.nonNull(taskExpectation)) {
      taskExpectation.execute(execution);
    }
  }
}
