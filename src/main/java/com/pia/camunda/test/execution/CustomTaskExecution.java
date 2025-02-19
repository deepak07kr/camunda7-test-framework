package com.pia.camunda.test.execution;

import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Setter
public class CustomTaskExecution implements TaskExecution {
  private Map<String, Object> variableMap = new HashMap<>();
  private Runnable runnable;

  @Override
  public void execute(DelegateExecution execution) {
    if (!variableMap.isEmpty()) {
      execution.setVariables(variableMap);
    }

    if (Objects.nonNull(runnable)) {
      runnable.run();
    }
  }
}
