package org.opentmf.camunda.test.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentmf.camunda.test.util.CamundaExpectationUtil;

@ExtendWith(MockitoExtension.class)
class ReceiveTaskManagerTest {

  @Mock private DelegateExecution delegateExecution;

  private ReceiveTaskManager manager;

  @BeforeEach
  void setUp() {
    manager = ReceiveTaskManager.getInstance();
    manager.clear();
    TaskExecutionRegistry.getInstance().clear();
  }

  @Test
  void informReceiveTask_withUnknownTaskId_logsWarningAndSkips() {
    manager.informReceiveTask("unknownTask", "pid", delegateExecution);

    assertNull(manager.getTaskContextMap().get("unknownTask"));
  }

  @Test
  void informReceiveTask_withRegisteredTask_setsContextCorrectly() {
    manager.registerReceiveTask("task1");

    manager.informReceiveTask("task1", "pid-1", delegateExecution);

    var ctx = manager.getTaskContextMap().get("task1");
    assertNotNull(ctx);
    assertTrue(ctx.getExecutionHelper().getAtomicBoolean().get());
    assertEquals("pid-1", ctx.getExecutionHelper().getProcessInstanceId());
    assertNotNull(ctx.getDelegateExecution());
  }

  @Test
  void registerReceiveTask_calledTwice_doesNotOverwrite() {
    manager.registerReceiveTask("task1");
    manager.informReceiveTask("task1", "pid-1", delegateExecution);

    manager.registerReceiveTask("task1");

    var ctx = manager.getTaskContextMap().get("task1");
    assertTrue(ctx.getExecutionHelper().getAtomicBoolean().get());
  }

  @Test
  void enqueueExpectation_addsToQueueAndOrder() {
    var expectation = new ReceiveTaskExpectations();
    expectation.setCorrelateMessage("msg");

    manager.enqueueExpectation("task1", expectation);

    assertNotNull(manager.getTaskExpectationsMap().get("task1"));
    assertEquals(1, manager.getTaskExpectationsMap().get("task1").size());
    assertFalse(manager.getReceiveTaskOrder().isEmpty());
  }

  @Test
  void clear_resetsQueuesAndContextState() {
    manager.registerReceiveTask("task1");
    manager.informReceiveTask("task1", "pid-1", delegateExecution);

    var expectation = new ReceiveTaskExpectations();
    expectation.setCorrelateMessage("msg");
    manager.enqueueExpectation("task1", expectation);

    manager.clear();

    assertTrue(manager.getReceiveTaskOrder().isEmpty());
    assertTrue(manager.getTaskExpectationsMap().isEmpty());
    var ctx = manager.getTaskContextMap().get("task1");
    assertNotNull(ctx);
    assertFalse(ctx.getExecutionHelper().getAtomicBoolean().get());
    assertNull(ctx.getExecutionHelper().getProcessInstanceId());
  }

  @Test
  void runAll_withNoQueuedTasks_completesImmediately() {
    assertDoesNotThrow(() -> manager.runAll());
  }

  @Test
  void enqueueExpectation_withExecutionConsumer_storesConsumer() {
    AtomicBoolean consumerInvoked = new AtomicBoolean(false);

    CamundaExpectationUtil.registerMessageCatchExecutionListener()
        .withTaskId("task1")
        .withCorrelationMessage("msg")
        .withExecutionConsumer(exec -> consumerInvoked.set(true))
        .create();

    var queue = manager.getTaskExpectationsMap().get("task1");
    assertNotNull(queue);
    var expectation = queue.peek();
    assertNotNull(expectation);
    assertNotNull(expectation.getExecutionConsumer());
  }

  @Test
  void enqueueExpectation_withRunnable_storesRunnable() {
    AtomicBoolean runnableInvoked = new AtomicBoolean(false);

    CamundaExpectationUtil.registerMessageCatchExecutionListener()
        .withTaskId("task1")
        .withCorrelationMessage("msg")
        .withRunnable(() -> runnableInvoked.set(true))
        .create();

    var queue = manager.getTaskExpectationsMap().get("task1");
    assertNotNull(queue);
    var expectation = queue.peek();
    assertNotNull(expectation);
    assertNotNull(expectation.getRunnable());
  }

  @Test
  void enqueueExpectation_withNullVariableMap_doesNotThrow() {
    CamundaExpectationUtil.registerMessageCatchExecutionListener()
        .withTaskId("task1")
        .withCorrelationMessage("msg")
        .withVariableMap(null)
        .create();

    var queue = manager.getTaskExpectationsMap().get("task1");
    assertNotNull(queue);
  }
}
