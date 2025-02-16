package com.pia.camunda.test.plugin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

import com.pia.camunda.test.context.CustomManagement;
import com.pia.camunda.test.context.CustomManagementRepository;
import com.pia.camunda.test.helper.ReceiveTaskHelper;
import com.pia.camunda.test.helper.ServiceTaskHelper;
import com.pia.camunda.test.integration.BaseBpmIT;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Integration tests for the {@link BpmnTaskListenerPlugin ReceiveTaskParseListenerPlugin}
 * class. This class tests the behavior of the plugin when it is enabled and the BPMN process is
 * started. The tests simulate the process start, the registration of receive tasks, and the
 * completion of the process. The BPMN process used in these tests is defined in the
 * WF_Sample_WaitInvocation.bpmn file. It contains two receive tasks and sends a message at the end
 * of each task.
 *
 * @author Yusuf Bozkurt
 */
@Sql(scripts = "classpath:db/create-table.sql", executionPhase = BEFORE_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "desired.port=8999")
class BpmnTaskParseListenerPluginIT extends BaseBpmIT {

  private static final String PDK_WF_SAMPLE_WAIT_INVOCATION = "WF_Sample_WaitInvocation";
  private static final String TASK_ID_WAIT_STATE_BEFORE = "waitStateBefore";
  private static final String TASK_ID_WAIT_STATE_AFTER = "waitStateAfter";
  private static final String TASK_ID_SERVICE_TASK_EXPECTATION = "service_task_expectation";
  private static final String MESSAGE_RECEIVE_TASK = "job-sub-process-completed";

  private static final ReceiveTaskHelper RECEIVE_TASK_HELPER = ReceiveTaskHelper.getInstance();
  private static final ServiceTaskHelper SERVICE_TASK_HELPER = ServiceTaskHelper.getInstance();

  @Autowired
  private CustomManagementRepository repository;


  @Test
  void testBpmnFileDeployment_withValidBpmnDefinitions_deploySuccessfully() {
    ProcessDefinition sampleWaitInvocation = validateDeployment(PDK_WF_SAMPLE_WAIT_INVOCATION);
    Assertions.assertEquals(PDK_WF_SAMPLE_WAIT_INVOCATION, sampleWaitInvocation.getKey());
  }

  /**
   * Tests the start of the BPMN process with valid expectations for the receive tasks. It verifies
   * that the process waits for the next step after the start.
   */
  @Test
  void testBpmnProcessStart_withReceiveTaskValidExpectations_waitingNextStep() {
    // Given
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, getWaitStateBeforeVariableMap());

    // When
    ProcessInstance instance = startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION);

    // Then
    RECEIVE_TASK_HELPER.assertWaiting(TASK_ID_WAIT_STATE_AFTER);
    assertNotNull(instance);
  }

  /**
   * Tests the start of the BPMN process with valid expectations for the receive tasks. It verifies
   * that the process is completed after the start.
   */
  @Test
  void testBpmnProcessStart_withReceiveTaskValidExpectations_completedProcess() {
    // Given
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, getWaitStateBeforeVariableMap());
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_AFTER, MESSAGE_RECEIVE_TASK, getWaitStateAfterVariableMap(true));

    // When
    ProcessInstance instance = startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION);

    // Then
    assertProcessEnded(instance);
  }

  @Test
  void testBpmnProcessStart_withoutExpectedOutputVariable_throwsProcessEngineException() {
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, getWaitStateBeforeMistakeVariableMap());
    Assertions.assertThrows(
        ProcessEngineException.class, () -> startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION));
  }

  @Test
  void testBpmnProcessStart_withUnxpectedOutputVariableValue_createsIncident() {
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, Map.of("status", "unexpected"));
    ProcessInstance instance = startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION);
    assertIncidentCreated(instance);
  }

  @Test
  void
      testBpmnProcessStart_withReceiveTaskValidExpectationsAndIsFinalStateIsFalse_ProcessWaitingAfterState() {
    // Given
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, getWaitStateBeforeVariableMap());
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_AFTER, MESSAGE_RECEIVE_TASK, getWaitStateAfterVariableMap(false));

    // When
    startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION);

    // Then
    RECEIVE_TASK_HELPER.assertWaiting(TASK_ID_WAIT_STATE_AFTER);
  }

  @Test
  void
      testBpmnProcessStart_withReceiveTaskValidExpectationsAndIsFinalStateIsFalseAfterThatItIsTrue_CompletedProcess() {
    // Given
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, getWaitStateBeforeVariableMap());
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_AFTER, MESSAGE_RECEIVE_TASK, getWaitStateAfterVariableMap(false));
    RECEIVE_TASK_HELPER.register(
        TASK_ID_WAIT_STATE_AFTER, MESSAGE_RECEIVE_TASK, getWaitStateAfterVariableMap(true));

    // When
    ProcessInstance instance = startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION);

    // Then
    assertProcessEnded(instance);
  }

  @Test
  void testBpmnProcessStart_withServiceTaskListener_CompletedProcess() {
    String entityId = UUID.randomUUID().toString();
    // Given
    RECEIVE_TASK_HELPER.register(
            TASK_ID_WAIT_STATE_BEFORE, MESSAGE_RECEIVE_TASK, getWaitStateBeforeVariableMap("expectation"));
    SERVICE_TASK_HELPER.register(
        TASK_ID_SERVICE_TASK_EXPECTATION, () -> repository.saveAndFlush(getEntity(entityId, "acknowledge")), Map.of("entityId", entityId));
    RECEIVE_TASK_HELPER.register(
            TASK_ID_WAIT_STATE_AFTER, MESSAGE_RECEIVE_TASK, getWaitStateAfterVariableMap(true));

    // When
    ProcessInstance instance = startProcessInstance(PDK_WF_SAMPLE_WAIT_INVOCATION);

    // Then
    assertProcessEnded(instance);
  }

  private CustomManagement getEntity(String id, String status) {
    var entity = new CustomManagement();
    entity.setId(id);
    entity.setStatus(status);
    return entity;
  }

  private Map<String, Object> getWaitStateBeforeVariableMap(String status) {
    return Map.of("status", status);
  }

  private Map<String, Object> getWaitStateBeforeVariableMap() {
    return getWaitStateBeforeVariableMap("success");
  }

  private Map<String, Object> getWaitStateAfterVariableMap(boolean isFinalState) {
    return Map.of("isFinalState", isFinalState);
  }

  private Map<String, Object> getWaitStateBeforeMistakeVariableMap() {
    return Map.of("mistake", "success");
  }

  /**
   * Returns the variables that are used to start the process instance. In this case, an empty map
   * is returned.
   *
   * @return A map of variables used to start the process instance.
   */
  @Override
  public Map<String, Object> getStartProcessVariables() {
    return new HashMap<>();
  }
}
