package com.pia.camunda.test.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pia.camunda.test.configuration.EnableReceiveTaskListenerPlugin;
import com.pia.camunda.test.helper.ReceiveTaskHelper;
import com.pia.camunda.IncidentLoggerPlugin;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

/**
 * BaseBpmIT is an abstract base class for all BPM integration tests. This class provides common
 * setup code and utility methods for all derived tests. It initializes Camunda's RepositoryService
 * and RuntimeService, provides a method to validate deployments, start process instances and check
 * if a process has ended.
 *
 * @author Yusuf BOZKURT
 */
@EnableReceiveTaskListenerPlugin
@Import(IncidentLoggerPlugin.class)
public abstract class BaseBpmIT implements BpmIT {

  private RepositoryService repositoryService;
  private RuntimeService runtimeService;

  @BeforeEach
  protected void beforeEach() {
    repositoryService = BpmnAwareTests.repositoryService();
    runtimeService = BpmnAwareTests.runtimeService();
  }

  protected final ProcessDefinition validateDeployment(String processDefinitionKey) {
    ProcessDefinition processDefinition =
        repositoryService
            .createProcessDefinitionQuery()
            .latestVersion()
            .processDefinitionKey(processDefinitionKey)
            .singleResult();
    Assertions.assertNotNull(processDefinition);
    return processDefinition;
  }

  protected final ProcessInstance startProcessInstance(String processDefinitionKey) {
    return startProcessInstance(processDefinitionKey, getStartProcessVariables());
  }

  protected final ProcessInstance startProcessInstance(
      String processDefinitionKey, Map<String, Object> startProcessVariables) {
    ProcessInstance processInstance =
        runtimeService.startProcessInstanceByKey(processDefinitionKey, startProcessVariables);
    ReceiveTaskHelper.getInstance().run();
    return processInstance;
  }

  protected final void assertProcessEnded(ProcessInstance instance) {
    await("Await_isEnded")
        .pollInterval(500, TimeUnit.MILLISECONDS)
        .atMost(60, TimeUnit.SECONDS)
        .until(
            () ->
                runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(instance.getId())
                        .singleResult()
                    == null);
  }

  protected final void assertIncidentCreated(
      ProcessInstance processInstance, String incidentMessage) {
    List<Incident> incidents =
        await("assertIncidentCreated(" + incidentMessage + ")")
            .pollInterval(10, TimeUnit.SECONDS)
            .atMost(120, TimeUnit.SECONDS)
            .until(() -> this.queryIncidents(processInstance), list -> !list.isEmpty());
    assertTrue(
        incidents.stream()
            .anyMatch(
                incident ->
                    incidentMessage == null
                        || (incident.getIncidentMessage() != null
                            && incident.getIncidentMessage().contains(incidentMessage))));
  }

  protected final void assertIncidentCreated(ProcessInstance processInstance) {
    assertIncidentCreated(processInstance, null);
  }

  private List<Incident> queryIncidents(ProcessInstance instance) {
    List<Incident> incidents =
        runtimeService
            .createIncidentQuery()
            .processInstanceId(instance.getProcessInstanceId())
            .list();

    List<Incident> rootIncidents =
        incidents.stream()
            .filter(
                incident ->
                    StringUtils.hasText(incident.getRootCauseIncidentId())
                        && !incident.getId().equals(incident.getRootCauseIncidentId()))
            .map(
                incident ->
                    runtimeService
                        .createIncidentQuery()
                        .incidentId(incident.getRootCauseIncidentId())
                        .singleResult())
            .collect(Collectors.toList());

    return Stream.concat(incidents.stream(), rootIncidents.stream()).collect(Collectors.toList());
  }
}
