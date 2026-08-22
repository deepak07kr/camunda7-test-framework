package org.opentmf.camunda.test.lock;

import java.util.List;
import org.cibseven.bpm.engine.externaltask.LockedExternalTask;
import org.cibseven.bpm.engine.test.assertions.bpmn.BpmnAwareTests;

/**
 * Produces the "somebody else holds my task now" situation deterministically — no sleeping until a
 * lock times out.
 *
 * <p>The scenario every external-task worker must survive: it locked a task, worked it, and by the
 * time it reports back the lock is gone — expired, or the task was re-locked by another worker.
 * Reproducing that by picking a tiny lock duration and sleeping past it makes a test slow AND
 * racy. This steward does it in two engine calls: {@link #expireLock(String)} drops the lock the
 * moment the test says so, and {@link #stealAs(String, String)} hands the task to a named second
 * worker — after which the original worker's {@code complete}/{@code failure} is rejected by the
 * engine, which is exactly the rejection the worker's classification logic needs to see.
 *
 * <pre>{@code
 * LockedExternalTask task = LockSteward.lockAs("worker-A", "render", 60_000);
 * LockSteward.expireLock(task.getId());
 * LockSteward.stealAs("worker-B", "render");
 * // worker-A's complete(...) now throws — assert your ladder classifies it as lock loss
 * }</pre>
 *
 * <p>Static utilities over the embedded engine's own {@code ExternalTaskService} (via {@code
 * BpmnAwareTests}), consistent with how {@code BaseBpmIT} reaches every other engine service.
 *
 * @author Yusuf BOZKURT
 */
public final class LockSteward {

  private static final long DEFAULT_LOCK_MILLIS = 60_000L;

  private LockSteward() {}

  /** Locks one task of the topic for the named worker, with the default 60s lock. */
  public static LockedExternalTask lockAs(String workerId, String topicName) {
    return lockAs(workerId, topicName, DEFAULT_LOCK_MILLIS);
  }

  /** Locks one task of the topic for the named worker. Fails fast when the topic has none. */
  public static LockedExternalTask lockAs(String workerId, String topicName, long lockMillis) {
    List<LockedExternalTask> locked =
        BpmnAwareTests.externalTaskService()
            .fetchAndLock(1, workerId)
            .topic(topicName, lockMillis)
            .execute();
    if (locked.isEmpty()) {
      throw new IllegalStateException(
          "No external task waiting on topic '"
              + topicName
              + "' — is the process actually at that task? assertProcessWaiting(...) first");
    }
    return locked.get(0);
  }

  /**
   * Drops the task's lock NOW — the deterministic stand-in for "the lock duration elapsed". The
   * previous holder keeps its stale reference and its next report is the interesting moment.
   */
  public static void expireLock(String externalTaskId) {
    BpmnAwareTests.externalTaskService().unlock(externalTaskId);
  }

  /**
   * Re-locks one waiting task of the topic for a DIFFERENT worker — combined with {@link
   * #expireLock(String)} this is the full "your task was taken" scenario.
   */
  public static LockedExternalTask stealAs(String newWorkerId, String topicName) {
    return lockAs(newWorkerId, topicName);
  }
}
