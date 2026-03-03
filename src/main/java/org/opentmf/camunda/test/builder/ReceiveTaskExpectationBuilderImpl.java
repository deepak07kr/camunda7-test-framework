package org.opentmf.camunda.test.builder;

import org.opentmf.camunda.test.execution.ReceiveTaskExecution;
import org.opentmf.camunda.test.helper.ReceiveTaskExpectations;
import org.opentmf.camunda.test.helper.ReceiveTaskManager;
import org.opentmf.camunda.test.helper.TaskExecutionRegistry;

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
    for (int i = 0; i < count; i++) {
      var taskExecution = new ReceiveTaskExecution();
      TaskExecutionRegistry.getInstance().register(taskId, taskExecution);

      var expectation = new ReceiveTaskExpectations();
      expectation.setRunnable(customRunnable);
      expectation.setExecutionConsumer(executionConsumer);
      expectation.setVariableMap(variableMap);
      expectation.setCorrelateMessage(message);
      ReceiveTaskManager.getInstance().enqueueExpectation(taskId, expectation);
    }
  }
}
