# Camunda7 Test Framework

The BPMNs contain logic in decision trees and in scripts, but not only that, they contain mandatory information on which input and/or output variables are expected before and after each task. There can be numerous combinations how a BPMN flow can continue depending on what sort of variables is set within the task implementations. 

Before deploying a BPMN to the Camunda-7 server, it is essential to test the BPMN flows with all possible combinations of expectations, input and output variables so that we can be sure that the flow will work as expected in the production environment.

Writing integration tests for the BPMN flows have always been a challenge. There is no straightforward way to perform tests for the BPMN flows. However, Camunda-7 platform contain a large set of APIs which makes it possible to write integration tests with the help of an embedded Camunda-7 server in test classpath.

Our Camunda-7 Test Framework makes it very easy to write an integration test for Camunda-7 BPM platform through intelligent use of its API. What it does briefly is:

- When the Spring Boot context is being initialized, it parses the BPMN files and silently registers listeners for each task.
- These listeners are just placeholders which does nothing, just lets the flow continue.
- In your test class, you have the ability to register your own listeners for each task.
- These registrations essentially are not real task listeners, but they are just adding lambdas to the already registered but initially empty set of tasks to be executed. They can be used to add some behavior to the flow, like setting variables, doing DB inserts, creating incidents etc.
- A special sort of registration is also available for receive tasks. You can register a receive-task with a correlation message name and a variable map to set so that the flow will automatically continue.
- After each test, these registrations will be removed implicitly, so that other tests can start with a clean state.
- A flow is started by supplying an initial set of workflow variables.
- Before a flow is started, you should provide your expectations for each task. An expectation can be a mock-server expectation or providing some initial data in a database table. 
- Useful methods to assert whether the test is successful or failed are provided and should be used in the final stage of the test method.

> **Note:** The embedded Camunda7 server configured by the library to be used in the tests is completely identical with the `camunda7-openid-microservice` opentmf server project. 

## Usage

Add this test dependency to your project:

### Maven Dependency
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.opentmf</groupId>
      <artifactId>opentmf-versions</artifactId>
      <type>pom</type>
      <scope>import</scope>
      <version>RELEASE</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

```xml 
<dependency>
  <groupId>org.opentmf.camunda</groupId>
  <artifactId>camunda7-test-framework</artifactId>
  <scope>test</scope>
</dependency> 
``` 

### Use `@EnableBpmnTaskListenerPlugin`

This annotation causes the library to parse the BPMNs when the Spring Boot context is being prepared, enabling all the magic.

### Alternative: Extend `BaseBpmIT`

The library provides an abstract base class named BaseBpmIT, which already adds the `@EnableBpmnTaskListenerPlugin` annotation.

### Example Test

Below is a simple example of how to use the helper in your integration tests:

 ```java 
import java.util.Map;
import java.util.UUID;

import org.opentmf.camunda.test.model.EventType;

import static org.opentmf.camunda.test.util.CamundaExpectationUtil.registerReceiveTaskExecutionListener;
import static org.opentmf.camunda.test.util.CamundaExpectationUtil.registerTaskExecutionListener;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MyBpmFlowIT extends BaseBpmIT {

  private static final String TASK_ID_RECEIVE_TASK = "myReceiveTaskId";
  private static final String TASK_ID_SERVICE_TASK = "myServiceTaskId";
  private static final String MESSAGE_NAME = "myMessageName";
  private static final String PDK_SAMPLE_BPMN_FLOW = "processDefinitionKey";

  @Test
  void testMyBpmnFlow_withDefaultStartVariables_finishesSuccessfully() {
    // Prepare mock server expectations for your flow
    setupYourMockServerExpectations();
    String entityId = UUID.randomUUID().toString();

    // Register task variables and correlation message for receive tasks
    registerReceiveTaskExecutionListener()
            .withTaskId(TASK_ID_RECEIVE_TASK)
            .withVariableMap(Map.of("status", "success"))
            .withCorrelationMessage(MESSAGE_NAME)
            .create();

    // Register a runnable to be executed before the task starts
    registerTaskExecutionListener()
            .withEventType(EventType.START)
            .withTaskId(TASK_ID_SERVICE_TASK)
            .withRunnable(() -> repository.saveAndFlush(getEntity(entityId, "acknowledge")))
            .create();

    // Register variables to be set after the task ends
    registerTaskExecutionListener()
            .withEventType(EventType.END)
            .withTaskId(TASK_ID_SERVICE_TASK)
            .withVariableMap(Map.of("entityId", entityId))
            .create();

    // Use the library method to start the process
    ProcessInstance instance = startProcessInstance(PDK_SAMPLE_BPMN_FLOW,
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

### Overview of Key Classes

Here is a brief overview of the main classes used in the helper:

- `@EnableBpmnTaskListenerPlugin`: This annotation activates the `BpmnTaskListenerPlugin` during tests.
- `BpmnTaskParseListener`: This class is the main entry point of the helper. It listens to parsing events of
  BPMN processes and adds a listener to each Receive Task, Service Task, ExclusiveGateway etc. .
- `TaskExecutionRegistry`: This singleton class provides a method to register the expectation of a All Task.
- `ReceiveTaskManager` : This class is responsible for managing the expectations of Receive Tasks.
- `CamundaExpectationUtil` : This class provides utility methods to register the expectations of tasks.

By using the `TaskExecutionRegistry`, you can easily simulate the behavior of asynchronous service tasks and message tasks in your tests.

### Recommended Application Context
If you are using this library, most probably your application is an external Camunda Client, and most probably you are dependent on some set of BPMNs and your application deploys the BPMNs to Camunda 7 on startup with the help of [camunda7-bpmn-sync-service](https://github.com/opentmf/camunda7-bpmn-sync-service). So, your aim is to test the BPMN flows in certain IT tests, but maybe you want to disable Camunda 7 engine to be up and running in other IT tests.

What can you do?

Let's consider the following two application configurations:

#### application-it.yml
Let this one be the base application configuration for your IT tests. You can specify the following Camunda related settings:

```yaml
# starting with Camunda 7.24, this exclude became necessary
spring:
  autoconfigure:
    exclude:
    - org.camunda.bpm.spring.boot.starter.rest.CamundaBpmRestJerseyAutoConfiguration

camunda:
  bpm:
    enabled: false
    client:
      disable-auto-fetching: true
```

And your IT tests that exclude Camunda 7 Embedded engine should provide at least:

`@ActiveProfiles("it")`

#### application-camunda.yaml

This one should be specified in `@ActiveProfiles` section where you want to run Camunda BPMN IT tests.

```yaml
spring:
  autoconfigure:
    exclude: []

camunda:
  bpm:
    enabled: true
    client:
      baseUrl: ....
```

And your IT tests that must use this library should provide at least:

`@ActiveProfiles({"it", "camunda"})`

The latter profile settings will override the configuration that was set in the previous profiles.

## Version History

### 1.0.0
- Initial Version

### 1.0.1
- Updates Camunda to 7.22.0 together with related libraries.

### 1.0.2
- Updates Spring Boot to 3.4.0
- Updates Camunda Incident Logger to 1.0.2

### 1.0.3 (Backward Incompatible)
- Updates Spring Boot to 3.4.2
- Adds Custom Task Execution Listener for all tasks.
- Adds `TaskExecution` interface to execute the expectation of all tasks.
- Adds new methods to the `CamundaExpectationUtil` class.
- Adds new Class `TaskExecutionRegistry` to manage the expectations of all tasks.

### 1.0.4
- Updates Spring Boot to 3.4.4
- Updates Camunda to 7.23.0
- Updates Camunda Incident Logger to 1.0.3
- Initial open source version

### 1.0.5
- Updates Spring Boot to 3.5.6
- Updates Camunda to 7.24.0
- Updates Camunda Incident Logger to 1.0.4

### 1.0.6
- Updates Documentation
- Specifies `legacyJobRetryBehaviorEnabled=true` property in test scope