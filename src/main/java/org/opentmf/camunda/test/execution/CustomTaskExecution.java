package org.opentmf.camunda.test.execution;

import org.opentmf.camunda.test.helper.TaskExecutionRegistry;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Setter;
import org.cibseven.bpm.engine.delegate.BpmnError;
import org.cibseven.bpm.engine.delegate.DelegateExecution;

/**
 * The CustomTaskExecution class is a concrete implementation of the {@link TaskExecution}
 * interface. It encapsulates custom business logic that can be executed within the context of the
 * Camunda BPM engine. The class enables dynamic handling of process variables and the execution of
 * arbitrary business logic by providing a flexible mechanism to set variables and run custom code
 * in a defined execution context.
 *
 * <p>Key Features: - Supports setting multiple process variables to the current workflow execution
 * using a simple configuration map. - Allows the execution of a custom {@link Runnable} for
 * encapsulating business-specific tasks. - Integrates seamlessly into the Camunda BPM workflow
 * engine and follows the Strategy pattern for task execution.
 *
 * <p>Example Usage:
 *
 * <pre>{@code
 * CustomTaskExecution taskExecution = new CustomTaskExecution();
 * taskExecution.setVariableMap(Map.of("key1", "value1", "key2", "value2"));
 * taskExecution.setRunnable(() -> System.out.println("Executing custom task logic."));
 *
 * taskExecution.execute(delegateExecution); // Invoked by Camunda with execution context
 * }</pre>
 *
 * <p>Workflow Integration: - The process designer or developer can register this class dynamically
 * as a task executor in the workflow lifecycle using the {@code TaskExecutionRegistry}. - It can be
 * leveraged in scenarios where dynamic variables need to be pushed or lightweight computation is
 * required for a specific activity.
 *
 * <p>Design Pattern: This class implements the **Strategy Pattern**, enabling the workflow engine
 * to execute configurable logic dynamically without modifying the core process definitions. It
 * ensures flexibility for integrating multiple task implementations.
 *
 * @see TaskExecution
 * @see DelegateExecution
 * @see TaskExecutionRegistry
 * @author Yusuf BOZKURT
 */
@Setter
public class CustomTaskExecution implements TaskExecution {
  private Map<String, Object> variableMap = new HashMap<>();
  private Runnable runnable;
  private Consumer<DelegateExecution> executionConsumer;
  private String failureMessage;
  private String bpmnErrorCode;
  private Duration delay;

  @Override
  public void execute(DelegateExecution execution) {
    // Delay first: a "slow task" must be slow before it does anything observable.
    if (Objects.nonNull(delay)) {
      sleepQuietly(delay);
    }

    if (!variableMap.isEmpty()) {
      execution.setVariables(variableMap);
    }

    // Execute consumer first (has access to execution context)
    if (Objects.nonNull(executionConsumer)) {
      executionConsumer.accept(execution);
    }

    // Execute runnable after (no context access)
    if (Objects.nonNull(runnable)) {
      runnable.run();
    }

    // Scripted outcomes LAST: variables and consumers above still apply, so a failing
    // expectation can leave evidence behind before it blows up.
    if (Objects.nonNull(bpmnErrorCode)) {
      throw new BpmnError(bpmnErrorCode);
    }
    if (Objects.nonNull(failureMessage)) {
      throw new SimulatedTaskFailure(failureMessage);
    }
  }

  private static void sleepQuietly(Duration duration) {
    try {
      Thread.sleep(duration.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SimulatedTaskFailure("delay interrupted: " + e.getMessage());
    }
  }
}
