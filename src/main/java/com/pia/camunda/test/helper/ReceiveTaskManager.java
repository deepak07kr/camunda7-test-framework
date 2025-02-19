package com.pia.camunda.test.helper;

import static org.awaitility.Awaitility.await;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.springframework.util.CollectionUtils;

/**
 *
 *
 * <h1>ReceiveTaskManager</h1>
 *
 * <p>A high-level manager (Singleton) that tracks and processes "Receive Task" expectations in a
 * Camunda-based BPMN flow. This class:
 *
 * <ul>
 *   <li>Maintains a FIFO queue of Receive Task IDs to be processed.
 *   <li>Maps each Task ID to a queue of {@link ReceiveTaskExpectations}.
 *   <li>Keeps a {@link ReceiveTaskContext} for each Task ID with state like {@link
 *       ReceiveTaskExecutionHelper} and an {@code AtomicBoolean}.
 *   <li>Provides {@link #runAll()} to process tasks in order, waiting for them to be active and
 *       then correlating or running their logic.
 *   <li>Offers {@link #assertWaiting(String)} for verification in test scenarios.
 * </ul>
 *
 * <p><b>Thread Safety:</b> Uses {@link ConcurrentHashMap} and {@link ConcurrentLinkedQueue} to
 * handle concurrent access. If you have only single-thread usage, it still works fine.
 *
 * <p><b>Potential Enhancements:</b>
 *
 * <ul>
 *   <li>Scheduling periodic checks with a thread pool or {@code ScheduledExecutorService}.
 *   <li>Paralel işlem (multi-thread) ile her Receive Task ID'yi farklı iş parçacığında ele almak.
 *   <li>More advanced matching or filtering logic before correlation.
 * </ul>
 *
 * @author -- Original code by: You -- High-level refactoring & commentary by: ChatGPT
 */
@Slf4j
public class ReceiveTaskManager {

  private ReceiveTaskManager() {}

  private static final class InstanceHolder {
    private static final ReceiveTaskManager instance = new ReceiveTaskManager();
  }

  /**
   * Returns the singleton instance of ReceiveTaskManager using double-checked locking. Thread-safe
   * lazy initialization.
   */
  public static ReceiveTaskManager getInstance() {
    return InstanceHolder.instance;
  }

  /**
   * Wraps necessary context for a Receive Task:
   *
   * <ul>
   *   <li>The {@link ReceiveTaskExecutionHelper} (holding atomic boolean and processInstanceId)
   *   <li>Any extra metadata we might need (timestamps, states, etc.)
   * </ul>
   */
  @Getter
  private static class ReceiveTaskContext {
    private final ReceiveTaskExecutionHelper executionHelper;

    ReceiveTaskContext(String taskId) {
      this.executionHelper = new ReceiveTaskExecutionHelper();
      this.executionHelper.setReceiveTaskId(taskId);
      this.executionHelper.getAtomicBoolean().set(false);
    }
  }

  /** FIFO queue that tracks which receive task IDs are due to be processed by {@link #runAll()}. */
  private final Queue<String> receiveTaskOrder = new ConcurrentLinkedQueue<>();

  /** Maps each receive task ID to a queue of expectations. */
  private final Map<String, Queue<ReceiveTaskExpectations>> taskExpectationsMap =
      new ConcurrentHashMap<>();

  /**
   * Maps each receive task ID to a {@link ReceiveTaskContext} containing execution helper data and
   * readiness flags.
   */
  private final Map<String, ReceiveTaskContext> taskContextMap = new ConcurrentHashMap<>();

  /**
   * Initializes and registers a new "Receive Task" by creating a {@link ReceiveTaskContext}.
   *
   * @param receiveTaskId the unique ID of the Receive Task (BPMN activityId, for example)
   */
  public void registerReceiveTask(String receiveTaskId) {
    // If not present, create a new context
    taskContextMap.computeIfAbsent(
        receiveTaskId,
        (String id) -> {
          log.debug("Creating new ReceiveTaskContext for taskId={}", id);
          return new ReceiveTaskContext(id);
        });
  }

  public void informReceiveTask(String receiveTaskId, String processInstanceId) {
    ReceiveTaskContext context = taskContextMap.get(receiveTaskId);
    if (context == null) {
      log.warn("No context found for receiveTaskId={}. Skipping.", receiveTaskId);
      return;
    }
    context.getExecutionHelper().setProcessInstanceId(processInstanceId);
    context.getExecutionHelper().getAtomicBoolean().set(true);
  }

  /**
   * Adds expectations for a given receive task ID and enqueues that ID for processing.
   *
   * @param receiveTaskId the task ID
   * @param expectations the object describing correlation messages, runnables, variables, etc.
   */
  public void enqueueExpectation(String receiveTaskId, ReceiveTaskExpectations expectations) {
    taskExpectationsMap
        .computeIfAbsent(receiveTaskId, k -> new ConcurrentLinkedQueue<>())
        .add(expectations);

    receiveTaskOrder.offer(receiveTaskId);
  }

  /**
   * Processes all currently queued task IDs in a FIFO manner:
   *
   * <ol>
   *   <li>Pulls the next task ID from {@link #receiveTaskOrder}.
   *   <li>Awaits for it to be "ready" in Camunda sense (checked by {@code atomicBoolean} + runtime
   *       query).
   *   <li>Picks the next expectation from that ID's queue and executes correlation or a custom
   *       runnable.
   *   <li>Continues until the order queue is empty.
   * </ol>
   *
   * <p><b>Note:</b> If you want this run to happen periodically or in parallel threads, consider
   * hooking it up to a scheduled executor or event-based trigger.
   */
  public void runAll() {
    while (!receiveTaskOrder.isEmpty()) {
      String taskId = receiveTaskOrder.poll();
      if (taskId == null) {
        break;
      }
      processExpectation(taskId);
    }
    log.debug("Completed runAll(). No more tasks in queue.");
  }

  /**
   * Asserts that the process is indeed waiting at the given task ID. Useful in test scenarios, then
   * it deletes the process instance for cleanup.
   *
   * @param receiveTaskId the ID of the receive task
   */
  public void assertWaiting(String receiveTaskId) {
    ReceiveTaskContext context = taskContextMap.get(receiveTaskId);
    if (context == null) {
      throw new IllegalStateException("No context found for receiveTaskId=" + receiveTaskId);
    }

    await("waitingForTaskId[" + receiveTaskId + "]")
        .atMost(60, TimeUnit.SECONDS)
        .until(() -> isTaskCurrentlyWaiting(context));

    ProcessInstance processInstance =
        BpmnAwareTests.runtimeService()
            .createProcessInstanceQuery()
            .processInstanceId(context.getExecutionHelper().getProcessInstanceId())
            .singleResult();

    BpmnAwareTests.assertThat(processInstance).isWaitingAt(receiveTaskId);

    // Cleanup: remove the process instance
    BpmnAwareTests.runtimeService()
        .deleteProcessInstance(
            context.getExecutionHelper().getProcessInstanceId(),
            "Test finished, cleaning up process instance");

    log.info("Asserted waiting for taskId={}, then deleted its process instance.", receiveTaskId);
  }

  /**
   * Processes the expectations for a given task by verifying the task's state,
   * retrieving expectations, and executing the corresponding logic.
   *
   * @param taskId Identifier for the task whose expectations are to be processed.
   */
  private void processExpectation(String taskId) {
    ReceiveTaskContext context = taskContextMap.get(taskId);
    if (context == null) {
      log.warn("No context for taskId={}. Skipping.", taskId);
      return;
    }

    // 1) Wait until the engine is actually paused/blocked at this task
    await("waitingForTaskId[" + taskId + "]")
        .atMost(60, TimeUnit.SECONDS)
        .until(() -> isTaskCurrentlyWaiting(context));

    // 2) Grab one expectation
    Queue<ReceiveTaskExpectations> queue = taskExpectationsMap.get(taskId);
    if (queue == null || queue.isEmpty()) {
      log.debug("No expectations left for taskId={}.", taskId);
      return;
    }
    ReceiveTaskExpectations expectation = queue.poll();
    if (expectation == null) {
      return;
    }

    // 3) Execute correlation or custom logic
    if (expectation.getRunnable() != null) {
      doRunRunnableExpectation(taskId, expectation);
    } else {
      doMessageCorrelation(taskId, expectation);
    }
  }

  /** Runs a custom {@link Runnable} from the expectation's configuration. */
  private void doRunRunnableExpectation(String taskId, ReceiveTaskExpectations expectation) {
    log.debug("Running custom Runnable for taskId={}", taskId);
    expectation.getRunnable().run();
    log.info("Runnable completed for taskId={}", taskId);
  }

  /** Performs a Camunda message correlation with the given {@link ReceiveTaskExpectations}. */
  private void doMessageCorrelation(String taskId, ReceiveTaskExpectations expectation) {
    ReceiveTaskContext context = taskContextMap.get(taskId);
    if (context == null) {
      log.warn("Context is null for taskId={}. Cannot correlate.", taskId);
      return;
    }
    String processInstanceId = context.getExecutionHelper().getProcessInstanceId();

    log.debug(
        "Correlating message [{}] for taskId={} with processInstanceId={}",
        expectation.getCorrelateMessage(),
        taskId,
        processInstanceId);

    BpmnAwareTests.runtimeService()
        .createMessageCorrelation(expectation.getCorrelateMessage())
        .processInstanceId(processInstanceId)
        .setVariables(expectation.getVariableMap())
        .correlate();

    log.info(
        "Message correlation done for taskId={} with message [{}]",
        taskId,
        expectation.getCorrelateMessage());
  }

  /**
   * Checks if this task is "currently waiting" in the Camunda engine by verifying both the atomic
   * boolean in {@link ReceiveTaskExecutionHelper} and an active process instance with matching
   * activity ID.
   *
   * @param context the {@link ReceiveTaskContext} for this task
   * @return true if the engine is paused at this task ID and the atomic boolean is true
   */
  private boolean isTaskCurrentlyWaiting(ReceiveTaskContext context) {
    if (context == null) {
      return false;
    }

    ReceiveTaskExecutionHelper helper = context.getExecutionHelper();
    if (helper == null) {
      return false;
    }

    List<ProcessInstance> processInstances =
        BpmnAwareTests.runtimeService()
            .createProcessInstanceQuery()
            .activityIdIn(helper.getReceiveTaskId())
            .list();

    var result = false;
    if (!CollectionUtils.isEmpty(processInstances)) {
      var isReady = helper.getAtomicBoolean().get();
      if (isReady) {
        String processInstanceId = helper.getProcessInstanceId();
        result =
            processInstances.stream()
                .anyMatch(pi -> pi.getProcessInstanceId().equals(processInstanceId));
      }
    }

    return result;
  }
}
