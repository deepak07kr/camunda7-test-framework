package org.opentmf.camunda.test.builder;

import org.opentmf.camunda.test.execution.CustomTaskExecution;
import org.opentmf.camunda.test.helper.TaskExecutionRegistry;
import org.opentmf.camunda.test.model.EventType;

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
    for (int i = 0; i < count; i++) {
      var taskExecution = new CustomTaskExecution();
      taskExecution.setVariableMap(variableMap);
      taskExecution.setRunnable(customRunnable);
      taskExecution.setExecutionConsumer(executionConsumer);
      if (eventType == null) {
        TaskExecutionRegistry.getInstance().register(taskId, taskExecution);
      } else {
        TaskExecutionRegistry.getInstance().register(taskId, eventType, taskExecution);
      }
    }
  }
}
