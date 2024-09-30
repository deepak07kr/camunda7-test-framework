package com.pia.camunda.test.listener;

import com.pia.camunda.test.helper.ReceiveTaskExecutionHelper;
import com.pia.camunda.test.helper.ReceiveTaskHelper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiveTaskListenerTest {

    @Mock
    private DelegateExecution delegateExecution;

    private ReceiveTaskListener receiveTaskListener;

    @BeforeEach
    void setUp() {
        receiveTaskListener = new ReceiveTaskListener();
    }

    @Test
    void test_notifyReceiveTaskListener() {
        String activityId = "activityId";
        String processInstanceId = "processInstanceId";

        ReceiveTaskHelper receiveTaskHelper = ReceiveTaskHelper.getInstance();
        ReceiveTaskExecutionHelper executionHelper = new ReceiveTaskExecutionHelper();
        executionHelper.setReceiveTaskId(activityId);
        executionHelper.setProcessInstanceId(processInstanceId);
        executionHelper.setAtomicBoolean(new AtomicBoolean(false));
        receiveTaskHelper.getReceiveTaskExecutionHelperMap().put(activityId, executionHelper);

        when(delegateExecution.getCurrentActivityId()).thenReturn(activityId);
        when(delegateExecution.getProcessInstanceId()).thenReturn(processInstanceId);

        receiveTaskListener.notify(delegateExecution);

        Assertions.assertTrue(executionHelper.getAtomicBoolean().get());
    }
}
