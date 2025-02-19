package com.pia.camunda.test.builder;

import com.pia.camunda.test.execution.CustomTaskExecution;
import com.pia.camunda.test.helper.TaskExecutionRegistry;
import com.pia.camunda.test.model.EventType;

public class TaskExpectationBuilderImpl
    extends AbstractTaskExpectationBuilder<TaskExpectationBuilder>
    implements TaskExpectationBuilder {

  protected EventType eventType;

  @Override
  public TaskExpectationBuilder withEventType(EventType eventType) {
    this.eventType = eventType;
    return this;
  }

  @Override
  public void create() {
    var taskExecution = new CustomTaskExecution();
    taskExecution.setVariableMap(variableMap);
    taskExecution.setRunnable(customRunnable);
    if (eventType == null) {
      TaskExecutionRegistry.getInstance().register(taskId, taskExecution);
    } else {
      TaskExecutionRegistry.getInstance().register(taskId, eventType, taskExecution);
    }
  }
}
