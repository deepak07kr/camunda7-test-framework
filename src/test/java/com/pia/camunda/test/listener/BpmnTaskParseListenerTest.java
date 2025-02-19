package com.pia.camunda.test.listener;

import static org.mockito.Mockito.*;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BpmnTaskParseListenerTest {

    @Mock
    private Element userTaskElement;

    @Mock
    private ScopeImpl scope;

    @Mock
    private ActivityImpl activity;

    private BpmnTaskParseListener bpmnTaskParseListener;

    @BeforeEach
    void setUp() {
        bpmnTaskParseListener = new BpmnTaskParseListener();
    }

    @Test
    void parseReceiveTaskTest() {
        when(activity.getId()).thenReturn("activityId");

        bpmnTaskParseListener.parseReceiveTask(userTaskElement, scope, activity);

    verify(activity, times(1))
        .addListener(eq(ExecutionListener.EVENTNAME_START), any(CustomTaskExecutionListener.class));
  }

  @Test
  void parseServiceTaskTest() {
    bpmnTaskParseListener.parseServiceTask(userTaskElement, scope, activity);

    verify(activity, times(1))
        .addListener(eq(ExecutionListener.EVENTNAME_START), any(CustomTaskExecutionListener.class));
    verify(activity, times(1))
        .addListener(eq(ExecutionListener.EVENTNAME_END), any(CustomTaskExecutionListener.class));
  }
}
