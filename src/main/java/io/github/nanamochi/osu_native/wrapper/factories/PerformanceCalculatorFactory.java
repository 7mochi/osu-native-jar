package io.github.nanamochi.osu_native.wrapper.factories;

import io.github.nanamochi.osu_native.wrapper.calculators.performance.*;
import io.github.nanamochi.osu_native.wrapper.objects.Ruleset;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;

public class PerformanceCalculatorFactory {
  static {
    NativeLoader.ensureLoaded();
  }

  public static PerformanceCalculator create(Ruleset ruleset) {
    switch (ruleset.getRulesetId()) {
      case 0 -> {
        return OsuPerformanceCalculator.create();
      }
      case 1 -> {
        return TaikoPerformanceCalculator.create();
      }
      case 2 -> {
        return CatchPerformanceCalculator.create();
      }
      case 3 -> {
        return ManiaPerformanceCalculator.create();
      }
      default ->
          throw new IllegalArgumentException("Unsupported ruleset ID: " + ruleset.getRulesetId());
    }
  }
}
