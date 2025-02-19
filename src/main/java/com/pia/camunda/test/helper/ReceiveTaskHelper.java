package com.pia.camunda.test.helper;

import lombok.Getter;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * The ReceiveTaskHelper class is a utility class that provides functionality to manage and manipulate Receive Tasks.
 * This includes functionalities like registering tasks, executing them, asserting their waiting status, and more.
 * It also maintains state for these tasks.
 *
 * @author Yusuf BOZKURT
 */
@Deprecated
@Getter
public class ReceiveTaskHelper {

  private static ReceiveTaskHelper instance = null;

  private final Queue<String> receiveTaskOrder = new LinkedList<>();
  private final Map<String, Queue<ReceiveTaskExpectations>> receiveTaskHelperMap = new HashMap<>();
  private final Map<String, ReceiveTaskExecutionHelper> receiveTaskExecutionHelperMap = new HashMap<>();

  private ReceiveTaskHelper() {
  }

  /**
   * Singleton instance getter.
   *
   * @return The singleton instance of this class.
   */
  public static ReceiveTaskHelper getInstance() {
    if (instance == null) {
      instance = new ReceiveTaskHelper();
    }
    return instance;
  }

  /**
   * Registers a task with its necessary data.
   *
   * @param receiveTaskId Identifier for the Receive Task.
   * @param message Message to be correlated with the task.
   * @param variableMap Map of variables to be used in the task.
   */
  public void register(String receiveTaskId, String message, Map<String, Object> variableMap) {
    ReceiveTaskExpectations expectations = new ReceiveTaskExpectations();
    expectations.setReceiveTaskId(receiveTaskId);
    expectations.setCorrelateMessage(message);
    expectations.setVariableMap(variableMap);
    receiveTaskHelperMap.computeIfAbsent(receiveTaskId, k-> new LinkedList<>()).add(expectations);
    receiveTaskOrder.offer(receiveTaskId);
  }

  /**
   * Registers a task with a Runnable to be executed.
   *
   * @param receiveTaskId Identifier for the Receive Task.
   * @param runnable Runnable to be executed as the task.
   */
  public void register(String receiveTaskId, Runnable runnable) {
    ReceiveTaskExpectations expectations = new ReceiveTaskExpectations();
    expectations.setReceiveTaskId(receiveTaskId);
    expectations.setRunnable(runnable);
    receiveTaskHelperMap.computeIfAbsent(receiveTaskId, k-> new LinkedList<>()).add(expectations);
    receiveTaskOrder.offer(receiveTaskId);
  }

  /**
   * Executes the registered tasks in the order they were registered.
   */
  public void run() {
    while (!receiveTaskOrder.isEmpty()) {
      String receiveTaskId = receiveTaskOrder.remove();
      run(receiveTaskId);
    }
  }

  private void run(String receiveTaskId) {
    await("await_receiveTaskId[" + receiveTaskId + "]").atMost(60, TimeUnit.SECONDS)
        .until(() -> isWaitingThisStep(receiveTaskExecutionHelperMap.get(receiveTaskId)));

    ReceiveTaskExpectations expectations = receiveTaskHelperMap.get(receiveTaskId).remove();

    if (Objects.nonNull(expectations.getRunnable())) {
      expectations.getRunnable().run();
    } else {
      BpmnAwareTests.runtimeService()
          .createMessageCorrelation(expectations.getCorrelateMessage())
          .processInstanceId(
              receiveTaskExecutionHelperMap.get(receiveTaskId).getProcessInstanceId())
          .setVariables(expectations.getVariableMap())
          .correlate();
    }
  }

  /**
   * Asserts if a task is waiting and then deletes the test process.
   *
   * @param receiveTaskId Identifier for the Receive Task.
   */
  public void assertWaiting(String receiveTaskId) {
    await("await_receiveTaskId[" + receiveTaskId + "]").atMost(60, TimeUnit.SECONDS)
        .until(() -> isWaitingThisStep(receiveTaskExecutionHelperMap.get(receiveTaskId)));

    ReceiveTaskExecutionHelper helper = receiveTaskExecutionHelperMap.get(receiveTaskId);
    ProcessInstance processInstance = BpmnAwareTests.runtimeService()
        .createProcessInstanceQuery()
        .processInstanceId(helper.getProcessInstanceId())
        .singleResult();

    BpmnAwareTests
        .assertThat(processInstance)
        .isWaitingAt(receiveTaskId);

    BpmnAwareTests.runtimeService().deleteProcessInstance(helper.getProcessInstanceId(), "Delete Test Process");
  }

  private boolean isWaitingThisStep(ReceiveTaskExecutionHelper helper) {
    if (Objects.nonNull(helper)) {
      List<ProcessInstance> processInstances = BpmnAwareTests.runtimeService()
          .createProcessInstanceQuery()
          .activityIdIn(helper.getReceiveTaskId())
          .list();

      if(!CollectionUtils.isEmpty(processInstances)){
        return helper.getAtomicBoolean().get()
            && processInstances.stream().anyMatch(processInstance ->
                processInstance.getProcessInstanceId().equals(helper.getProcessInstanceId()));
      }
    }
    return false;
  }

}
