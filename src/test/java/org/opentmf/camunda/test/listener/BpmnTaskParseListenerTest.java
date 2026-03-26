package org.opentmf.camunda.test.listener;

import static org.mockito.Mockito.*;

import org.cibseven.bpm.engine.delegate.ExecutionListener;
import org.cibseven.bpm.engine.impl.core.variable.mapping.IoMapping;
import org.cibseven.bpm.engine.impl.pvm.process.ActivityImpl;
import org.cibseven.bpm.engine.impl.pvm.process.ScopeImpl;
import org.cibseven.bpm.engine.impl.util.xml.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentmf.camunda.test.helper.ReceiveTaskManager;

@ExtendWith(MockitoExtension.class)
class BpmnTaskParseListenerTest {

  @Mock private Element element;
  @Mock private ScopeImpl scope;
  @Mock private ActivityImpl activity;
  @Mock private IoMapping ioMapping;

  private BpmnTaskParseListener listener;

  @BeforeEach
  void setUp() {
    listener = new BpmnTaskParseListener();
    ReceiveTaskManager.getInstance().clear();
  }

  private void verifyStartAndEndListeners() {
    verify(activity, times(1))
        .addListener(eq(ExecutionListener.EVENTNAME_START), any(CustomTaskExecutionListener.class));
    verify(activity, times(1))
        .addListener(eq(ExecutionListener.EVENTNAME_END), any(CustomTaskExecutionListener.class));
  }

  @Test
  void parseReceiveTaskTest() {
    when(activity.getId()).thenReturn("receiveTaskId");
    listener.parseReceiveTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseServiceTaskTest() {
    listener.parseServiceTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseStartEventTest() {
    listener.parseStartEvent(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseEndEventTest() {
    listener.parseEndEvent(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseExclusiveGatewayTest() {
    listener.parseExclusiveGateway(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseInclusiveGatewayTest() {
    listener.parseInclusiveGateway(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseParallelGatewayTest() {
    listener.parseParallelGateway(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseScriptTaskTest() {
    listener.parseScriptTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBusinessRuleTaskTest() {
    listener.parseBusinessRuleTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseTaskTest() {
    listener.parseTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseManualTaskTest() {
    listener.parseManualTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseUserTaskTest() {
    listener.parseUserTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundaryTimerEventDefinitionTest() {
    listener.parseBoundaryTimerEventDefinition(element, true, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundaryErrorEventDefinitionTest() {
    ActivityImpl nestedActivity = mock(ActivityImpl.class);
    listener.parseBoundaryErrorEventDefinition(element, true, activity, nestedActivity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseSubProcessTest() {
    listener.parseSubProcess(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseCallActivityTest() {
    listener.parseCallActivity(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseSendTaskTest() {
    listener.parseSendTask(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseMultiInstanceLoopCharacteristicsTest() {
    Element multiElement = mock(Element.class);
    listener.parseMultiInstanceLoopCharacteristics(element, multiElement, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseIntermediateTimerEventDefinitionTest() {
    listener.parseIntermediateTimerEventDefinition(element, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseIntermediateSignalCatchEventDefinitionTest() {
    listener.parseIntermediateSignalCatchEventDefinition(element, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundarySignalEventDefinitionTest() {
    listener.parseBoundarySignalEventDefinition(element, true, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseEventBasedGatewayTest() {
    listener.parseEventBasedGateway(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseTransactionTest() {
    listener.parseTransaction(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseCompensateEventDefinitionTest() {
    listener.parseCompensateEventDefinition(element, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseIntermediateThrowEventTest() {
    listener.parseIntermediateThrowEvent(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundaryEventTest() {
    listener.parseBoundaryEvent(element, scope, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseIntermediateMessageCatchEventDefinitionTest() {
    when(activity.getId()).thenReturn("msgCatchId");
    listener.parseIntermediateMessageCatchEventDefinition(element, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundaryMessageEventDefinitionTest() {
    when(activity.getId()).thenReturn("boundaryMsgId");
    listener.parseBoundaryMessageEventDefinition(element, true, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundaryEscalationEventDefinitionTest() {
    listener.parseBoundaryEscalationEventDefinition(element, true, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseBoundaryConditionalEventDefinitionTest() {
    listener.parseBoundaryConditionalEventDefinition(element, true, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseIntermediateConditionalEventDefinitionTest() {
    listener.parseIntermediateConditionalEventDefinition(element, activity);
    verifyStartAndEndListeners();
  }

  @Test
  void parseConditionalStartEventForEventSubprocessTest() {
    listener.parseConditionalStartEventForEventSubprocess(element, activity, true);
    verifyStartAndEndListeners();
  }

  @Test
  void parseIoMappingTest() {
    listener.parseIoMapping(element, activity, ioMapping);
    verifyStartAndEndListeners();
  }
}
