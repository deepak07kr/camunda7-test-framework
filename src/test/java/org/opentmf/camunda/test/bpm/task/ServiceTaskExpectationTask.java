package org.opentmf.camunda.test.bpm.task;

import org.opentmf.camunda.test.context.CustomManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.cibseven.bpm.client.task.ExternalTask;
import org.cibseven.bpm.client.task.ExternalTaskHandler;
import org.cibseven.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ExternalTaskSubscription(topicName = "serviceTaskExpectation")
public class ServiceTaskExpectationTask implements ExternalTaskHandler {

  private final CustomManagementRepository repository;

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    log.info("Service task expectation task is executed.");
    String entityId = externalTask.getVariable("entityId");

    var entity = repository.findById(entityId).orElseThrow(() -> new RuntimeException("Entity not found."));
    entity.setServiceTaskListenerTriggered(true);
    repository.save(entity);

    if(entity.isServiceTaskListenerTriggered() && entity.isServiceTaskTriggered() && entity.getStatus().equals("completed")) {
      externalTaskService.complete(externalTask, Map.of("isFinalState", Boolean.TRUE));
    }
    externalTaskService.complete(externalTask, Map.of("isFinalState", Boolean.FALSE));

  }
}
