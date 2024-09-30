package com.pia.camunda.test.listener;


import com.pia.camunda.test.helper.ReceiveTaskHelper;
import com.pia.camunda.test.helper.ReceiveTaskExecutionHelper;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 * The ReceiveTaskListener class listens to execution events on a receive task in a BPMN process.
 * It is triggered when a receive task is started.
 *
 * @author Yusuf Bozkurt
 */
@Getter
public class ReceiveTaskListener implements ExecutionListener {

  /**
   * Method invoked when an execution event occurs.
   * It fetches the helper object for the execution's current activity and sets its processInstanceId and atomicBoolean fields.
   *
   * @param execution The execution context of the BPMN engine.
   */
  @Override
  public void notify(DelegateExecution execution) {
    ReceiveTaskHelper receiveTaskHelper = ReceiveTaskHelper.getInstance();
    ReceiveTaskExecutionHelper executionHelper = receiveTaskHelper.getReceiveTaskExecutionHelperMap().get(execution.getCurrentActivityId());
    executionHelper.setProcessInstanceId(execution.getProcessInstanceId());
    executionHelper.getAtomicBoolean().set(true);
  }
}
