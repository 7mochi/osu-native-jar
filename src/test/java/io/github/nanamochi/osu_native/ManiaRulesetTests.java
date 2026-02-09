package io.github.nanamochi.osu_native;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.nanamochi.osu_native.utils.File;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.ManiaDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.ManiaPerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.PerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.factories.DifficultyCalculatorFactory;
import io.github.nanamochi.osu_native.wrapper.factories.PerformanceCalculatorFactory;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("osu-native-jar – Mania ruleset tests")
public class ManiaRulesetTests {
  private static final String BEATMAP_RESOURCE = "5107047.osu";

  @Nested
  @DisplayName("Difficulty & performance attributes")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class OsuManiaAttributeTests {
    Stream<Arguments> cases() {
      return Stream.of(
          Arguments.of(
              List.of(),
              someScore(),
              new ExpectedDifficulty(11.627630733008322, 24779),
              new ExpectedPerformance(1566.2954965275737, 1566.2954965275737)));
    }

    @ParameterizedTest(name = "Difficulty attributes for mods = {0}")
    @MethodSource("cases")
    void testDifficultyAttributes(
        List<String> mods,
        ScoreInfo score,
        ExpectedDifficulty expected,
        ExpectedPerformance ignored)
        throws Exception {
      ManiaDifficultyAttributes diff =
          (ManiaDifficultyAttributes) runOsuMania(mods, score).difficulty();
      assertAll(
          () -> assertEquals(expected.starRating(), diff.getStarRating()),
          () -> assertEquals(expected.maxCombo(), diff.getMaxCombo()));
    }

    @ParameterizedTest(name = "Performance attributes for mods = {0}")
    @MethodSource("cases")
    void testPerformanceAttributes(
        List<String> mods,
        ScoreInfo score,
        ExpectedDifficulty ignored,
        ExpectedPerformance expected)
        throws Exception {
      ManiaPerformanceAttributes perf =
          (ManiaPerformanceAttributes) runOsuMania(mods, score).performance();
      assertAll(
          () -> assertEquals(expected.total(), perf.getTotal()),
          () -> assertEquals(expected.difficulty(), perf.getDifficulty()));
    }
  }

  record ExpectedDifficulty(double starRating, int maxCombo) {}

  record ExpectedPerformance(double total, double difficulty) {}

  record ManiaTestResult(DifficultyAttributes difficulty, PerformanceAttributes performance) {}

  private static ManiaTestResult runOsuMania(List<String> mods, ScoreInfo scoreInfo)
      throws Exception {
    try (Beatmap beatmap = Beatmap.fromFile(File.resourceToTempFile(BEATMAP_RESOURCE));
        Ruleset ruleset = Ruleset.fromId(3);
        var diffCalculator = DifficultyCalculatorFactory.create(ruleset, beatmap);
        var perfCalculator = PerformanceCalculatorFactory.create(ruleset);
        ModsCollection modsCollection = ModsCollection.create()) {
      for (String mod : mods) {
        modsCollection.add(Mod.create(mod));
      }

      var diff = diffCalculator.calculate(modsCollection);
      var perf = perfCalculator.calculate(ruleset, beatmap, modsCollection, scoreInfo, diff);

      return new ManiaTestResult(diff, perf);
    }
  }

  private static ScoreInfo someScore() {
    ScoreInfo score = new ScoreInfo();
    score.setAccuracy(0.9779);
    score.setMaxCombo(2142);
    score.setCountPerfect(18261);
    score.setCountGreat(6214);
    score.setCountGood(562);
    score.setCountOk(113);
    score.setCountMeh(73);
    score.setCountMiss(127);
    return score;
  }
}
