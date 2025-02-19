# Camunda Integration Test Framework

Receive Task Helper is a powerful auxiliary tool that simplifies the testing of BPMN processes on the Camunda BPM Platform. It offers a straightforward and effective way of handling Receive Tasks in your integration tests.

## Table of Contents

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)
    - [Annotation](#annotation)
    - [Example Test](#example-test)
- [Overview of Key Classes](#overview-of-key-classes)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

This project is an add-on for the Camunda BPM Platform. Therefore, what you will need are:

- Java 17 or higher
- Maven
- An existing microservice with Camunda BPM flow implementation.

### Installation
Add this test dependency to your project:

 ```xml 
 <dependency> 
   <groupId>com.pia.commons</groupId> 
   <artifactId>camunda-7-test-framework</artifactId> 
   <scope>test</scope>
 </dependency> 
 ``` 

## Usage

### Annotation

To use the helper in your tests, annotate your test class with `@EnableReceiveTaskListenerPlugin`. This enables the `ReceiveTaskParseListenerPlugin` during tests.

The library provides an abstract base class named BaseBpmIT, which already adds this annotation. 

### Example Test

Below is a simple example of how to use the helper in your integration tests:

 ```java 


import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ReceiveTaskHelperTest extends BaseBpmIT {

  private static final String RECEIVE_TASK_ID = "myTaskId";
  private static final String MESSAGE_NAME = "myMessageName";
  private static final String PDK_SAMPLE_BPMN_FLOW = "processDefinitionKey";
  private static final ReceiveTaskHelper RECEIVE_TASK_HELPER = ReceiveTaskHelper.getInstance();

  @Test
  void testMyBpmnFlow_withDefaultStartVariables_finishesSuccessfully() {
    // Prepare mock server expectations for your flow
    setupYourMockServerExpectations();

    // Register your task variables for your receive tasks
    ReceiveTaskHelper.getInstance().
            register("myTaskId", "myMessageName", Map.of("status", "success"));

    // Use the library method to start the process
    ProcessInstance instance = startProcessInstance("myProcessDefinitionKey",
            Map.of("orderId", "1", "orderItemId", "1"));

    // Use the library method to assert the process is ended successfully 
    assertProcessEnded(instance);
  }

  @Test
  void testMyBpmnFlow_withFailedWaitTask_createsIncident() {
    // Prepare mock server expectations for your flow
    setupYourMockServerExpectations();

    // Register your task variables for your receive tasks
    ReceiveTaskHelper.getInstance()
            .register("myTaskId", "myMessageName", Map.of("status", "failed"));

    // Use the library method to start the process
    ProcessInstance instance = startProcessInstance("myProcessDefinitionKey",
            Map.of("orderId", "1", "orderItemId", "1"));

    // Use the library method to assert the process is ended successfully 
    assertIncidentCreated(instance, "Wait task status is failed");
  }

  private void setupYourMockServerExpectations() {
    // Setup your mockserver expectations
  }

  // Example of preparing variable map
  private Map<String, Object> prepareReceiveTaskVariables() {
    return Map.of("status", "success");
  }


  @Override
  private Map<String, Object> getProcessVariables() {
    return Map.of(
            "productOrderID", UUID.randomUUID().toString(),
            "productOrderItemID", UUID.randomUUID().toString());
  }
} 
 ``` 

## Overview of Key Classes

Here is a brief overview of the main classes used in the helper:

- `EnableReceiveTaskListenerPlugin`: This annotation activates the `ReceiveTaskParseListenerPlugin` during tests.
- `ReceiveTaskParseListenerPlugin`: This class is the main entry point of the helper. It listens to parsing events of BPMN processes and adds a listener to each Receive Task.
- `ReceiveTaskHelper`: This singleton class provides a method to register the expectation of a Receive Task.

By using the `ReceiveTaskHelper`, you can easily simulate the behavior of asynchronous service tasks and message tasks in your tests.

## Version History

### 1.0.0
- Initial Version
### 1.0.1
- Updates Camunda to 7.22.0 together with related libraries.
### 1.0.2
- Updates Spring Boot to 3.4.0
- Updates Camunda Incident Logger to 1.0.2
