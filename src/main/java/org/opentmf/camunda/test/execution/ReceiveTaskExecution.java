package org.opentmf.camunda.test.execution;

import org.opentmf.camunda.test.helper.ReceiveTaskManager;
import org.camunda.bpm.engine.delegate.DelegateExecution;

/**
 * The {@code ReceiveTaskExecution} class is an implementation of the {@link TaskExecution}
 * interface used to handle the execution of "Receive Task" activities within a workflow process
 * managed by the Camunda BPM engine. This class provides logic specific to managing and notifying
 * the "Receive Task" event through the {@link ReceiveTaskManager}.
 *
 * <p>Receive tasks are typically used in BPM workflows when the process execution must pause and
 * wait for an external event or message to continue. This class ensures that such tasks are
 * informed effectively, enabling seamless interactions between external systems and the process
 * engine.
 *
 * <p><b>Core Functionality:</b> - Implements the {@link TaskExecution} interface, which defines the
 * contract for executing tasks within the BPM engine context. - Uses the singleton instance of
 * {@link ReceiveTaskManager} to delegate the task notification process.
 *
 * <p><b>Usage:</b> This class is invoked automatically by the BPM engine whenever a Receive Task
 * needs to be executed. It is not directly used by developers in typical applications.
 *
 * @author Yusuf BOZKURT
 */
public class ReceiveTaskExecution implements TaskExecution {
  @Override
  public void execute(DelegateExecution execution) {
    ReceiveTaskManager.getInstance()
        .informReceiveTask(execution.getCurrentActivityId(), execution.getProcessInstanceId());
  }
}
