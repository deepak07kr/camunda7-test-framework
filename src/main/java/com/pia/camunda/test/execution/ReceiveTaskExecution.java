package com.pia.camunda.test.execution;

import com.pia.camunda.test.helper.ReceiveTaskManager;
import org.camunda.bpm.engine.delegate.DelegateExecution;

public class ReceiveTaskExecution implements TaskExecution {
  @Override
  public void execute(DelegateExecution execution) {
    ReceiveTaskManager.getInstance()
        .informReceiveTask(execution.getCurrentActivityId(), execution.getProcessInstanceId());
  }
}
