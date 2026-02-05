package org.opentmf.camunda.test.listener;

import org.opentmf.camunda.test.helper.ReceiveTaskManager;
import java.util.Arrays;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.core.variable.mapping.IoMapping;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

/**
 * The `BpmnTaskParseListener` class is a custom implementation of the `AbstractBpmnParseListener`,
 * designed to extend and customize the parsing behavior of BPMN 2.0 process models within a
 * workflow engine context, such as Camunda. This class enables developers to attach custom
 * execution listeners to specific BPMN task types (`ReceiveTask` and `ServiceTask`) during the
 * parsing phase, allowing additional logic to be executed when specified task lifecycle events are
 * triggered.
 *
 * <p>This implementation is particularly useful in scenarios where additional instrumentation,
 * monitoring, or business logic needs to be injected into tasks dynamically at runtime.
 *
 * <p>## Key Features: 1. Attaches custom execution listeners to specified task types. 2. Supports
 * multiple predefined lifecycle events: "start" and "end". 3. Utilizes a singleton manager for
 * handling `ReceiveTask` registrations.
 *
 * @author Yusuf BOZKURT
 */
public class BpmnTaskParseListener extends AbstractBpmnParseListener {

  @Override
  public void parseReceiveTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
    ReceiveTaskManager.getInstance().registerReceiveTask(activity.getId());
  }

  @Override
  public void parseServiceTask(Element serviceTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseExclusiveGateway(
      Element exclusiveGwElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseInclusiveGateway(
      Element inclusiveGwElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseParallelGateway(
      Element parallelGwElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseScriptTask(Element scriptTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseBusinessRuleTask(
      Element businessRuleTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseTask(Element taskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseManualTask(Element manualTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseBoundaryTimerEventDefinition(
      Element timerEventDefinition, boolean interrupting, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseBoundaryErrorEventDefinition(
      Element errorEventDefinition,
      boolean interrupting,
      ActivityImpl activity,
      ActivityImpl nestedErrorEventActivity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseSubProcess(Element subProcessElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseCallActivity(
      Element callActivityElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseSendTask(Element sendTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseMultiInstanceLoopCharacteristics(
      Element activityElement,
      Element multiInstanceLoopCharacteristicsElement,
      ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseIntermediateTimerEventDefinition(
      Element timerEventDefinition, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseIntermediateSignalCatchEventDefinition(
      Element signalEventDefinition, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseBoundarySignalEventDefinition(
      Element signalEventDefinition, boolean interrupting, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseEventBasedGateway(
      Element eventBasedGwElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseTransaction(Element transactionElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseCompensateEventDefinition(
      Element compensateEventDefinition, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseIntermediateThrowEvent(
      Element intermediateEventElement, ScopeImpl scope, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseBoundaryEvent(
      Element boundaryEventElement, ScopeImpl scopeElement, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseIntermediateMessageCatchEventDefinition(
      Element messageEventDefinition, ActivityImpl activity) {
    addCustomExecutionListener(activity);
    ReceiveTaskManager.getInstance().registerReceiveTask(activity.getId());
  }

  @Override
  public void parseBoundaryMessageEventDefinition(
      Element element, boolean interrupting, ActivityImpl activity) {
    addCustomExecutionListener(activity);
    ReceiveTaskManager.getInstance().registerReceiveTask(activity.getId());
  }

  @Override
  public void parseBoundaryEscalationEventDefinition(
      Element escalationEventDefinition, boolean interrupting, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseBoundaryConditionalEventDefinition(
      Element element, boolean interrupting, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseIntermediateConditionalEventDefinition(
      Element conditionalEventDefinition, ActivityImpl activity) {
    addCustomExecutionListener(activity);
  }

  @Override
  public void parseConditionalStartEventForEventSubprocess(
      Element element, ActivityImpl conditionalActivity, boolean interrupting) {
    addCustomExecutionListener(conditionalActivity);
  }

  @Override
  public void parseIoMapping(
      Element extensionElements, ActivityImpl activity, IoMapping inputOutput) {
    addCustomExecutionListener(activity);
  }

  private void addCustomExecutionListener(ActivityImpl activity) {
    addCustomExecutionListener(
        activity, ExecutionListener.EVENTNAME_START, ExecutionListener.EVENTNAME_END);
  }

  private void addCustomExecutionListener(ActivityImpl activity, String... event) {
    var listener = new CustomTaskExecutionListener();
    Arrays.stream(event).forEach(s -> activity.addListener(s, listener));
  }
}
