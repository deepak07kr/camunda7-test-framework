package com.pia.camunda.test.builder;

import com.pia.camunda.test.execution.ReceiveTaskExecution;
import com.pia.camunda.test.helper.ReceiveTaskExpectations;
import com.pia.camunda.test.helper.ReceiveTaskManager;
import com.pia.camunda.test.helper.TaskExecutionRegistry;

public class ReceiveTaskExpectationBuilderImpl
    extends AbstractTaskExpectationBuilder<ReceiveTaskExpectationBuilder>
    implements ReceiveTaskExpectationBuilder {

  private String message;

  @Override
  public ReceiveTaskExpectationBuilder withCorrelationMessage(String msg) {
    this.message = msg;
    return this;
  }

  @Override
  public void create() {
    var taskExecution = new ReceiveTaskExecution();
    TaskExecutionRegistry.getInstance().register(taskId, taskExecution);

    var expectation = new ReceiveTaskExpectations();
    expectation.setRunnable(customRunnable);
    expectation.setVariableMap(variableMap);
    expectation.setCorrelateMessage(message);
    ReceiveTaskManager.getInstance().enqueueExpectation(taskId, expectation);
  }
}
