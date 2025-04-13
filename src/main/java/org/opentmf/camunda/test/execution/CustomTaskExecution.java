package org.opentmf.camunda.test.execution;

import org.opentmf.camunda.test.helper.TaskExecutionRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;

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

  @Override
  public void execute(DelegateExecution execution) {
    if (!variableMap.isEmpty()) {
      execution.setVariables(variableMap);
    }

    if (Objects.nonNull(runnable)) {
      runnable.run();
    }
  }
}
