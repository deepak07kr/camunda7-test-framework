package org.opentmf.camunda.test.clock;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.ProcessEngines;
import org.cibseven.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.cibseven.bpm.engine.impl.util.ClockUtil;

/**
 * Moves the engine's clock so time-driven BPMN behavior can be tested without waiting for time.
 *
 * <p>Timer events, follow-up dates, retry back-offs and every other due-date the engine computes
 * all read {@link ClockUtil}. Jumping that clock forward makes a {@code PT24H} timer due NOW —
 * the job executor picks it up on its next acquisition cycle, and a test that used to be
 * "impossible without an overnight run" becomes three lines:
 *
 * <pre>{@code
 * ProcessInstance instance = startProcessInstance("WF_With_A_24h_Timer");
 * EngineClock.jumpBy(Duration.ofHours(25));
 * assertProcessEnded(instance);
 * }</pre>
 *
 * <p>The clock is engine-global, so hygiene matters more than usual: {@code BaseBpmIT} resets it
 * before each test and {@code EngineChaosExtension} after each — a jumped clock must never leak
 * into a neighbouring test. Jump forward only: the engine tolerates a rewound clock poorly
 * (already-acquired jobs, history ordering), so {@link #jumpBy(Duration)} refuses negatives.
 *
 * @author Yusuf BOZKURT
 */
public final class EngineClock {

  private static volatile boolean pinned;

  private EngineClock() {}

  /** Jumps the engine clock forward by the given amount. Negative jumps are refused — see class doc. */
  public static void jumpBy(Duration amount) {
    if (amount.isNegative()) {
      throw new IllegalArgumentException(
          "The engine clock only jumps FORWARD (asked for "
              + amount
              + ") — rewinding confuses acquired jobs and history ordering; reset() returns to real time");
    }
    setTo(now().plus(amount));
  }

  /** Pins the engine clock to the given instant (forward of real time or not — caller's call). */
  public static void setTo(Instant instant) {
    ClockUtil.setCurrentTime(Date.from(instant));
    pinned = true;
    wakeJobExecutor();
  }

  /** What the engine currently believes "now" is. */
  public static Instant now() {
    return ClockUtil.getCurrentTime().toInstant();
  }

  /** Whether the clock is currently pinned/jumped rather than following real time. */
  public static boolean isPinned() {
    return pinned;
  }

  /** Returns the engine to real time. Test-hygiene hook; called by the framework between tests. */
  public static void reset() {
    ClockUtil.reset();
    pinned = false;
    wakeJobExecutor();
  }

  /**
   * Every clock move ends with a nudge to the job executor. Without it, an acquisition thread
   * that computed its next wake-up under the OLD clock can strand: after a jump-and-reset the
   * timestamp it sleeps toward sits hours in the future of real time, and every async job in the
   * suite silently waits it out — the clock feature would poison the tests that run after it.
   */
  private static void wakeJobExecutor() {
    ProcessEngine engine = ProcessEngines.getDefaultProcessEngine(false);
    if (engine == null) {
      return; // pure unit use — no engine to wake
    }
    if (engine.getProcessEngineConfiguration()
        instanceof ProcessEngineConfigurationImpl configuration) {
      var jobExecutor = configuration.getJobExecutor();
      if (jobExecutor != null && jobExecutor.isActive()) {
        jobExecutor.jobWasAdded();
      }
    }
  }
}
