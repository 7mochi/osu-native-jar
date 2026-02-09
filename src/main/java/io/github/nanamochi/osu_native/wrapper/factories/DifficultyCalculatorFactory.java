package io.github.nanamochi.osu_native.wrapper.factories;

import io.github.nanamochi.osu_native.wrapper.calculators.difficulty.*;
import io.github.nanamochi.osu_native.wrapper.objects.Beatmap;
import io.github.nanamochi.osu_native.wrapper.objects.Ruleset;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;

public class DifficultyCalculatorFactory {
  static {
    NativeLoader.ensureLoaded();
  }

  public static DifficultyCalculator<?> create(Ruleset ruleset, Beatmap beatmap) {
    switch (ruleset.getRulesetId()) {
      case 0 -> {
        return OsuDifficultyCalculator.create(ruleset, beatmap);
      }
      case 1 -> {
        return TaikoDifficultyCalculator.create(ruleset, beatmap);
      }
      case 2 -> {
        return CatchDifficultyCalculator.create(ruleset, beatmap);
      }
      case 3 -> {
        return ManiaDifficultyCalculator.create(ruleset, beatmap);
      }
      default ->
          throw new IllegalArgumentException("Unsupported ruleset ID: " + ruleset.getRulesetId());
    }
  }
}
