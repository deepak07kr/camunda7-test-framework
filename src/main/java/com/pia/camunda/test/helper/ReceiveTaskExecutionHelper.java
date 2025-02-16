package com.pia.camunda.test.helper;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Setter;

/**
 * The ReceiveTaskExecutionHelper class is a utility class designed to assist in the execution of
 * Receive Tasks. This class stores the unique identifiers for the task and its associated process
 * instance, and includes an AtomicBoolean to indicate the execution status of the task.
 *
 * @author Yusuf BOZKURT
 */
@Getter
@Setter
public class ReceiveTaskExecutionHelper {

  private String receiveTaskId;
  private String processInstanceId;
  private AtomicBoolean atomicBoolean = new AtomicBoolean();
}
