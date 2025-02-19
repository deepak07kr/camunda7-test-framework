package com.pia.camunda.test.helper;


import java.util.*;
import lombok.Getter;

/**
 * The ReceiveTaskHelper class is a utility class that provides functionality to manage and
 * manipulate Receive Tasks. This includes functionalities like registering tasks, executing them,
 * asserting their waiting status, and more. It also maintains state for these tasks.
 *
 * @author Yusuf BOZKURT
 */
@Deprecated
@Getter
public class ServiceTaskHelper {

  private static ServiceTaskHelper instance;

  private final Map<String, Queue<ServiceTaskExpectations>> serviceTaskHelperMap = new HashMap<>();

  private ServiceTaskHelper() {}

  /**
   * Singleton instance getter.
   *
   * @return The singleton instance of this class.
   */
  public static ServiceTaskHelper getInstance() {
    if (instance == null) {
      instance = new ServiceTaskHelper();
    }
    return instance;
  }

  /**
   * Registers a task with its necessary data.
   *
   * @param taskId Identifier for the Receive Task.
   * @param variableMap Map of variables to be used in the task.
   */
  public void register(String taskId, Map<String, Object> variableMap) {
    register(taskId, null, variableMap);
  }

  /**
   * Registers a task with a Runnable to be executed.
   *
   * @param taskId Identifier for the Receive Task.
   * @param runnable Runnable to be executed as the task.
   */
  public void register(String taskId, Runnable runnable) {
    register(taskId, runnable, null);
  }

  public void register(String taskId, Runnable runnable, Map<String, Object> variableMap) {
    var expectations = new ServiceTaskExpectations();
    expectations.setTaskId(taskId);
    if (variableMap != null) {
      expectations.setVariableMap(variableMap);
    }
    expectations.setRunnable(runnable);
    serviceTaskHelperMap.computeIfAbsent(taskId, k -> new LinkedList<>()).add(expectations);
  }

  public ServiceTaskExpectations getExpectation(String taskId) {
    if (serviceTaskHelperMap.containsKey(taskId)) {
      return serviceTaskHelperMap.get(taskId).remove();
    }
    return null;
  }
}
