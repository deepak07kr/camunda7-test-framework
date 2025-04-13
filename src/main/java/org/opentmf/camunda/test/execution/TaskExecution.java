package org.opentmf.camunda.test.execution;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface TaskExecution {

  /**
   * Executes custom business logic during a workflow process within a Camunda BPM engine context.
   * This method is invoked by the BPM engine to execute task-specific behavior defined by the
   * implementing class. The method receives a {@link DelegateExecution} object as a parameter,
   * providing access to the current execution context and process variables.
   *
   * @param execution The DelegateExecution object representing the current execution context.
   */
  void execute(DelegateExecution execution);
}
