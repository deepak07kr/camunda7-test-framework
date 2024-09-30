package com.pia.camunda.test.bpm.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ExternalTaskSubscription(topicName = "createIncidentTask")
public class CreateIncidentTask implements ExternalTaskHandler {

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    externalTaskService.handleFailure(
        externalTask.getId(),
        "Created incident on purpose",
        "This incident is created on purpose to demonstrate the camunda test library capability",
        0, 100L);
  }
}
