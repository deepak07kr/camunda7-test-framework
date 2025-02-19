package com.pia.camunda.test.execution;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface TaskExecution {

  void execute(DelegateExecution execution);
}
