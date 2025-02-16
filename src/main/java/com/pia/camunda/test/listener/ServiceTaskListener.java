package com.pia.camunda.test.listener;

import com.pia.camunda.test.helper.ServiceTaskExpectations;
import com.pia.camunda.test.helper.ServiceTaskHelper;
import java.util.Objects;
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
public class ServiceTaskListener implements ExecutionListener {

  /**
   * Method invoked when an execution event occurs. It fetches the helper object for the execution's
   * current activity and sets its processInstanceId and atomicBoolean fields.
   *
   * @param execution The execution context of the BPMN engine.
   */
  @Override
  public void notify(DelegateExecution execution) {
    var helper = ServiceTaskHelper.getInstance();
    ServiceTaskExpectations expectations = helper.getExpectation(execution.getCurrentActivityId());

    if (Objects.nonNull(expectations)) {
      if (!expectations.getVariableMap().isEmpty()) {
        execution.setVariables(expectations.getVariableMap());
      }

      if (Objects.nonNull(expectations.getRunnable())) {
        expectations.getRunnable().run();
      }
    }
  }
}
