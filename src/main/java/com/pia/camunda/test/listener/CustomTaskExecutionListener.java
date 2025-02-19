package com.pia.camunda.test.listener;

import java.util.Objects;

import com.pia.camunda.test.helper.TaskExecutionRegistry;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 * The ReceiveTaskListener class listens to execution events on a receive task in a BPMN process. It
 * is triggered when a receive task is started.
 *
 * @author Yusuf Bozkurt
 */
@Getter
public class CustomTaskExecutionListener implements ExecutionListener {

  /**
   * Method invoked when an execution event occurs. It fetches the helper object for the execution's
   * current activity and sets its processInstanceId and atomicBoolean fields.
   *
   * @param execution The execution context of the BPMN engine.
   */
  @Override
  public void notify(DelegateExecution execution) {
    var taskExpectation =
        TaskExecutionRegistry.getInstance().poll(execution.getCurrentActivityId(), execution.getEventName());
    if (Objects.nonNull(taskExpectation)) {
      taskExpectation.execute(execution);
    }
  }
}
