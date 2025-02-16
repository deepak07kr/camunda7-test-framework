package com.pia.camunda.test.helper;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * The ReceiveTaskExpectations class is a data class that encapsulates the necessary information for
 * a Receive Task. This includes a unique identifier for the task, a map of variables to be used in
 * the task, a message to be correlated with the task, and an optional Runnable object that can be
 * executed as the task.
 *
 * @author Yusuf BOZKURT
 */
@Getter
@Setter
public class ServiceTaskExpectations {

  private String taskId;
  private Map<String, Object> variableMap = new HashMap<>();
  private Runnable runnable;
}
