package com.pia.camunda.test.builder;

public interface ReceiveTaskExpectationBuilder
    extends BaseTaskExpectationBuilder<ReceiveTaskExpectationBuilder> {

  ReceiveTaskExpectationBuilder withCorrelationMessage(String msg);
}
