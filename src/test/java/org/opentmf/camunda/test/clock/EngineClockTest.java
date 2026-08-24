package org.opentmf.camunda.test.clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class EngineClockTest {

  private static final Instant FROZEN_TARGET = Instant.parse("2030-01-01T12:00:00Z");

  @AfterEach
  void backToRealTime() {
    EngineClock.reset();
  }

  @Test
  void jumpBy_refusesNegativeAmounts() {
    IllegalArgumentException refused =
        assertThrows(
            IllegalArgumentException.class, () -> EngineClock.jumpBy(Duration.ofHours(-1)));
    assertTrue(refused.getMessage().contains("FORWARD"));
    assertFalse(EngineClock.isPinned());
  }

  @Test
  void jumpBy_shiftsTheClock_andTimeKeepsAdvancing() throws InterruptedException {
    Instant realBefore = Instant.now();

    EngineClock.jumpBy(Duration.ofHours(2));

    Instant jumped = EngineClock.now();
    assertTrue(
        Duration.between(realBefore.plus(Duration.ofHours(2)), jumped).abs().toSeconds() < 60,
        "the clock must land ~2h ahead of real time, saw " + jumped);
    assertTrue(EngineClock.isPinned());

    Thread.sleep(150);
    long advancedMillis = Duration.between(jumped, EngineClock.now()).toMillis();
    assertTrue(
        advancedMillis >= 100,
        "a jumped clock must KEEP ADVANCING (a frozen one would show 0ms), saw "
            + advancedMillis
            + "ms");
  }

  @Test
  void jumpBy_accumulatesAcrossCalls() {
    Instant realBefore = Instant.now();

    EngineClock.jumpBy(Duration.ofHours(1));
    EngineClock.jumpBy(Duration.ofHours(1));

    assertTrue(
        EngineClock.now().isAfter(realBefore.plus(Duration.ofMinutes(119))),
        "two 1h jumps must accumulate to ~2h ahead");
  }

  @Test
  void freezeAt_stopsTime() throws InterruptedException {
    EngineClock.freezeAt(FROZEN_TARGET);

    assertEquals(FROZEN_TARGET, EngineClock.now());
    assertTrue(EngineClock.isPinned());

    Thread.sleep(120);
    assertEquals(FROZEN_TARGET, EngineClock.now(), "a frozen clock must not advance");
  }

  @Test
  void jumpBy_onAFrozenClock_movesTheFrozenPoint_butTimeStaysStopped()
      throws InterruptedException {
    EngineClock.freezeAt(FROZEN_TARGET);

    EngineClock.jumpBy(Duration.ofHours(1));

    Instant movedTarget = FROZEN_TARGET.plus(Duration.ofHours(1));
    assertEquals(movedTarget, EngineClock.now());
    Thread.sleep(120);
    assertEquals(movedTarget, EngineClock.now(), "jumping a frozen clock must not unfreeze it");
  }

  @Test
  void reset_returnsToRealTime() {
    EngineClock.jumpBy(Duration.ofDays(1));

    EngineClock.reset();

    assertFalse(EngineClock.isPinned());
    assertTrue(
        Duration.between(Instant.now(), EngineClock.now()).abs().toSeconds() < 5,
        "after reset the engine clock must follow real time again");
  }
}
