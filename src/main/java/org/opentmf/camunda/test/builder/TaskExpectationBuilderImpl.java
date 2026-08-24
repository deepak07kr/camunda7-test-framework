package org.opentmf.camunda.test.builder;

import org.opentmf.camunda.test.execution.CustomTaskExecution;
import org.opentmf.camunda.test.helper.TaskExecutionRegistry;
import org.opentmf.camunda.test.model.EventType;
import java.time.Duration;

public class TaskExpectationBuilderImpl
    extends AbstractTaskExpectationBuilder<TaskExpectationBuilder>
    implements TaskExpectationBuilder {

  protected EventType eventType;
  private String failureMessage;
  private String bpmnErrorCode;
  private Duration delay;

  @Override
  public TaskExpectationBuilder withEventType(EventType eventType) {
    this.eventType = eventType;
    return this;
  }

  @Override
  public TaskExpectationBuilder withFailure(String message) {
    if (bpmnErrorCode != null) {
      throw new IllegalStateException("An expectation fails OR raises a BPMN error, not both");
    }
    this.failureMessage = message;
    return this;
  }

  @Override
  public TaskExpectationBuilder withBpmnError(String errorCode) {
    if (failureMessage != null) {
      throw new IllegalStateException("An expectation fails OR raises a BPMN error, not both");
    }
    this.bpmnErrorCode = errorCode;
    return this;
  }

  @Override
  public TaskExpectationBuilder withDelay(Duration delay) {
    this.delay = delay;
    return this;
  }

  @Override
  public void create() {
    for (int i = 0; i < count; i++) {
      var taskExecution = new CustomTaskExecution();
      taskExecution.setVariableMap(variableMap);
      taskExecution.setRunnable(customRunnable);
      taskExecution.setExecutionConsumer(executionConsumer);
      taskExecution.setFailureMessage(failureMessage);
      taskExecution.setBpmnErrorCode(bpmnErrorCode);
      taskExecution.setDelay(delay);
      if (eventType == null) {
        TaskExecutionRegistry.getInstance().register(taskId, taskExecution);
      } else {
        TaskExecutionRegistry.getInstance().register(taskId, eventType, taskExecution);
      }
    }
  }
}
