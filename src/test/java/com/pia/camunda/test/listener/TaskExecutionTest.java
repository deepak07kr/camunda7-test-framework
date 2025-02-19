package com.pia.camunda.test.listener;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.pia.camunda.test.execution.TaskExecution;
import com.pia.camunda.test.helper.ReceiveTaskManager;
import com.pia.camunda.test.helper.TaskExecutionRegistry;
import com.pia.camunda.test.model.EventType;
import com.pia.camunda.test.util.CamundaExpectationUtil;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskExecutionTest {

  @Mock private DelegateExecution delegateExecution;

  private CustomTaskExecutionListener receiveTaskListener;

  @BeforeEach
  void setUp() {
    receiveTaskListener = new CustomTaskExecutionListener();
  }

  @Test
  void test_notifyReceiveTaskListener() {
    String activityId = "activityId";
    String processInstanceId = "processInstanceId";

    ReceiveTaskManager.getInstance().registerReceiveTask(activityId);
    CamundaExpectationUtil.registerReceiveTaskExecutionListener()
        .withTaskId(activityId)
        .withCorrelationMessage("mesage")
        .withVariableMap(Map.of())
        .create();

    when(delegateExecution.getCurrentActivityId()).thenReturn(activityId);
    when(delegateExecution.getProcessInstanceId()).thenReturn(processInstanceId);

    receiveTaskListener.notify(delegateExecution);

    var ctx = ReceiveTaskManager.getInstance().getTaskContextMap().get(activityId);
    Assertions.assertTrue(ctx.getExecutionHelper().getAtomicBoolean().get());

    TaskExecution execution = TaskExecutionRegistry.getInstance().poll(activityId, EventType.START);
    assertNull(execution);
  }

  @Test
  void test_notifyReceiveTaskListener_withInvalidTaskId() {
    String activityId = "activityId";

    ReceiveTaskManager.getInstance().registerReceiveTask(activityId);
    CamundaExpectationUtil.registerReceiveTaskExecutionListener()
        .withTaskId("invalidTaskId")
        .withCorrelationMessage("mesage")
        .withVariableMap(Map.of())
        .create();

    when(delegateExecution.getCurrentActivityId()).thenReturn(activityId);
    receiveTaskListener.notify(delegateExecution);

    var ctx = ReceiveTaskManager.getInstance().getTaskContextMap().get(activityId);
    assertFalse(ctx.getExecutionHelper().getAtomicBoolean().get());

    TaskExecution execution = TaskExecutionRegistry.getInstance().poll(activityId, EventType.START);
    assertNull(execution);
  }

  @Test
  void test_notifyCustomTaskListener() {
    String activityId = "activityId";
    String eventName = "start";

    CamundaExpectationUtil.registerTaskExecutionListener()
        .withEventType(EventType.START)
        .withTaskId(activityId)
        .withVariableMap(Map.of())
        .create();

    when(delegateExecution.getCurrentActivityId()).thenReturn(activityId);
    when(delegateExecution.getEventName()).thenReturn(eventName);

    receiveTaskListener.notify(delegateExecution);

    TaskExecution execution = TaskExecutionRegistry.getInstance().poll(activityId, eventName);
    assertNull(execution);
  }
}
