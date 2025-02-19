package com.pia.camunda.test.util;

import com.pia.camunda.test.builder.ReceiveTaskExpectationBuilder;
import com.pia.camunda.test.builder.ReceiveTaskExpectationBuilderImpl;
import com.pia.camunda.test.builder.TaskExpectationBuilder;
import com.pia.camunda.test.builder.TaskExpectationBuilderImpl;

public class CamundaExpectationUtil {
  private CamundaExpectationUtil() {}

  public static TaskExpectationBuilder createTaskExpectation() {
    return new TaskExpectationBuilderImpl();
  }

  public static ReceiveTaskExpectationBuilder createReceiveTaskExpectation() {
    return new ReceiveTaskExpectationBuilderImpl();
  }
}
