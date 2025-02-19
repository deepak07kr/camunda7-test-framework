package com.pia.camunda.test.builder;


import java.util.Map;

public interface BaseTaskExpectationBuilder<T extends BaseTaskExpectationBuilder<T>> {
  T withTaskId(String taskId);

  T withVariableMap(Map<String, Object> variableMap);

  T withRunnable(Runnable runnable);

  void create();
}
