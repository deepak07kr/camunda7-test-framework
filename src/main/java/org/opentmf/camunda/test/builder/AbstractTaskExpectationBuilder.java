package org.opentmf.camunda.test.builder;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.cibseven.bpm.engine.delegate.DelegateExecution;

public abstract class AbstractTaskExpectationBuilder<T extends BaseTaskExpectationBuilder<T>>
    implements BaseTaskExpectationBuilder<T> {

  protected String taskId;
  protected Map<String, Object> variableMap = new HashMap<>();
  protected Runnable customRunnable;
  protected Consumer<DelegateExecution> executionConsumer;
  protected int count = 1;
  protected String failureMessage;
  protected String bpmnErrorCode;
  protected java.time.Duration delay;

  @SuppressWarnings("unchecked")
  @Override
  public T withTaskId(String taskId) {
    this.taskId = taskId;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withVariableMap(Map<String, Object> vars) {
    if (vars != null) {
      this.variableMap.putAll(vars);
    }
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withRunnable(Runnable runnable) {
    this.customRunnable = runnable;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withExecutionConsumer(Consumer<DelegateExecution> consumer) {
    this.executionConsumer = consumer;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withCount(int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be at least 1");
    }
    this.count = count;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withFailure(String message) {
    if (bpmnErrorCode != null) {
      throw new IllegalStateException("An expectation fails OR raises a BPMN error, not both");
    }
    this.failureMessage = message;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withBpmnError(String errorCode) {
    if (failureMessage != null) {
      throw new IllegalStateException("An expectation fails OR raises a BPMN error, not both");
    }
    this.bpmnErrorCode = errorCode;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T withDelay(java.time.Duration delay) {
    this.delay = delay;
    return (T) this;
  }
}
