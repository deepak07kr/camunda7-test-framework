package org.opentmf.camunda.test.helper;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.camunda.bpm.engine.delegate.DelegateExecution;

/**
 * The ReceiveTaskExpectations class is a data class that encapsulates the necessary information for
 * a Receive Task. This includes a unique identifier for the task, a map of variables to be used in
 * the task, a message to be correlated with the task, and optional Runnable or Consumer objects
 * that can be executed as the task.
 *
 * @author Yusuf BOZKURT
 */
@Getter
@Setter
public class ReceiveTaskExpectations {

  private String receiveTaskId;
  private Map<String, Object> variableMap = new HashMap<>();
  private String correlateMessage;
  private Runnable runnable;
  private Consumer<DelegateExecution> executionConsumer;
}
