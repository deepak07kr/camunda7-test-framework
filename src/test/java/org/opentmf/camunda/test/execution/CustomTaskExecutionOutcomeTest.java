package org.opentmf.camunda.test.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.Map;
import org.cibseven.bpm.engine.delegate.BpmnError;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

class CustomTaskExecutionOutcomeTest {

  @Test
  void failureMessage_throwsTheNamedType_afterVariablesApplied() {
    DelegateExecution execution = mock(DelegateExecution.class);
    CustomTaskExecution taskExecution = new CustomTaskExecution();
    taskExecution.setVariableMap(Map.of("evidence", true));
    taskExecution.setFailureMessage("scripted boom");

    SimulatedTaskFailure failure =
        assertThrows(SimulatedTaskFailure.class, () -> taskExecution.execute(execution));

    assertEquals("scripted boom", failure.getMessage());
    // Evidence-before-explosion: the variables landed even though the task then failed.
    verify(execution).setVariables(Map.of("evidence", true));
  }

  @Test
  void bpmnErrorCode_raisesTheBpmnError() {
    DelegateExecution execution = mock(DelegateExecution.class);
    CustomTaskExecution taskExecution = new CustomTaskExecution();
    taskExecution.setBpmnErrorCode("FAILED");

    BpmnError error = assertThrows(BpmnError.class, () -> taskExecution.execute(execution));

    assertEquals("FAILED", error.getErrorCode());
  }

  @Test
  void delay_holdsTheActivityBeforeAnythingElse() {
    DelegateExecution execution = mock(DelegateExecution.class);
    CustomTaskExecution taskExecution = new CustomTaskExecution();
    taskExecution.setDelay(Duration.ofMillis(120));

    long before = System.nanoTime();
    taskExecution.execute(execution);
    long elapsedMillis = (System.nanoTime() - before) / 1_000_000;

    assertTrue(elapsedMillis >= 100, "expected >=100ms of scripted delay, saw " + elapsedMillis);
  }
}
