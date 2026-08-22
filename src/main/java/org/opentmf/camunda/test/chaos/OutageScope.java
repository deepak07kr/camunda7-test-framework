package org.opentmf.camunda.test.chaos;

/**
 * The blast radius of an {@link EngineOutage}.
 *
 * <p>{@code ALL} simulates a dead engine: every REST call is refused. The selective scopes cut a
 * single leg of the external-task protocol so a test can drive one failure mode deterministically:
 * {@code FETCH_AND_LOCK} starves the client's poll loop while everything else works, and {@code
 * COMPLETION} lets the client fetch and work tasks but refuses {@code complete}/{@code
 * failure}/{@code bpmnError} reports — the exact shape a worker's retry ladder has to survive.
 *
 * @author Yusuf BOZKURT
 */
public enum OutageScope {

  /** Every request below {@code /engine-rest} is refused — the engine is "down". */
  ALL,

  /** Only {@code POST /external-task/fetchAndLock} is refused — the poll loop starves. */
  FETCH_AND_LOCK,

  /**
   * Only task outcome reports are refused — {@code /external-task/{id}/complete}, {@code
   * /failure} and {@code /bpmnError}. Fetching keeps working, reporting does not.
   */
  COMPLETION
}
