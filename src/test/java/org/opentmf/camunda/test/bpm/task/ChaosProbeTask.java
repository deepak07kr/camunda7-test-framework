package org.opentmf.camunda.test.bpm.task;

import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.cibseven.bpm.client.task.ExternalTask;
import org.cibseven.bpm.client.task.ExternalTaskHandler;
import org.cibseven.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

/**
 * The well-behaved worker of the chaos ITs: completes immediately. Everything interesting about
 * it happens OUTSIDE — whether its polls reach the engine at all is what {@code EngineOutage}
 * decides and {@code ExternalTaskProbe} measures.
 */
@Slf4j
@Component
@ExternalTaskSubscription(topicName = "chaosProbe")
public class ChaosProbeTask implements ExternalTaskHandler {

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    log.info("Chaos probe task executed for instance {}.", externalTask.getProcessInstanceId());
    externalTaskService.complete(externalTask);
  }
}
