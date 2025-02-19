package com.pia.camunda.test.builder;


import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTaskExpectationBuilder<T extends BaseTaskExpectationBuilder<T>>
    implements BaseTaskExpectationBuilder<T> {

  protected String taskId;
  protected Map<String, Object> variableMap = new HashMap<>();
  protected Runnable customRunnable;

  @SuppressWarnings("unchecked")
  @Override
  public T withTaskId(String taskId) {
    this.taskId = taskId;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withVariableMap(Map<String, Object> vars) {
    if (vars != null) {
      this.variableMap.putAll(vars);
    }
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withRunnable(Runnable runnable) {
    this.customRunnable = runnable;
    return (T) this;
  }
}
