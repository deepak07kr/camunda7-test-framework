package com.pia.camunda.test.listener;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiveTaskParseListenerTest {

    @Mock
    private Element userTaskElement;

    @Mock
    private ScopeImpl scope;

    @Mock
    private ActivityImpl activity;

    private ReceiveTaskParseListener receiveTaskParseListener;

    @BeforeEach
    void setUp() {
        receiveTaskParseListener = new ReceiveTaskParseListener();
    }

    @Test
    void parseReceiveTaskTest() {
        when(activity.getId()).thenReturn("activityId");

        receiveTaskParseListener.parseReceiveTask(userTaskElement, scope, activity);

        verify(activity, times(1)).addListener(eq(ExecutionListener.EVENTNAME_START), any(ReceiveTaskListener.class));
    }
}
