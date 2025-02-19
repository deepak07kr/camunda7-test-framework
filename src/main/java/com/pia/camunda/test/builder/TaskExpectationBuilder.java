package com.pia.camunda.test.builder;

import com.pia.camunda.test.model.EventType;

public interface TaskExpectationBuilder extends BaseTaskExpectationBuilder<TaskExpectationBuilder> {

  TaskExpectationBuilder withEventType(EventType eventType);
}
