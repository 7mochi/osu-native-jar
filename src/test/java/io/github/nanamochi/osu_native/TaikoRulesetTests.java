package io.github.nanamochi.osu_native;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.nanamochi.osu_native.utils.File;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.TaikoDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.PerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.TaikoPerformanceAttributes;
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

@DisplayName("osu-native-jar – Taiko ruleset tests")
public class TaikoRulesetTests {
  private static final String BEATMAP_RESOURCE = "221923.osu";

  @Nested
  @DisplayName("Difficulty & performance attributes")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class OsuTaikoAttributeTests {
    Stream<Arguments> cases() {
      return Stream.of(
          Arguments.of(
              List.of("DT"),
              someScore(),
              new ExpectedDifficulty(
                  5.826375388637023,
                  453,
                  0,
                  1.4625032919966257,
                  0,
                  0,
                  0,
                  1.8585410552067943e-8,
                  0.7117837850730536,
                  0),
              new ExpectedPerformance(
                  437.12346889778064, 240.57633493439914, 196.5471339633815, 91.33286105656317)));
    }

    @ParameterizedTest(name = "Difficulty attributes for mods = {0}")
    @MethodSource("cases")
    void testDifficultyAttributes(
        List<String> mods,
        ScoreInfo score,
        ExpectedDifficulty expected,
        ExpectedPerformance ignored)
        throws Exception {
      TaikoDifficultyAttributes diff =
          (TaikoDifficultyAttributes) runOsuTaiko(mods, score).difficulty();
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
      TaikoPerformanceAttributes perf =
          (TaikoPerformanceAttributes) runOsuTaiko(mods, score).performance();
      assertAll(
          () -> assertEquals(expected.total(), perf.getTotal()),
          () -> assertEquals(expected.difficulty(), perf.getDifficulty()));
    }
  }

  record ExpectedDifficulty(
      double starRating,
      int maxCombo,
      double mechanicalDifficulty,
      double rhythmDifficulty,
      double readingDifficulty,
      double colourDifficulty,
      double staminaDifficulty,
      double monoStaminaFactor,
      double consistencyFactor,
      double staminaTopStrains) {}

  record ExpectedPerformance(
      double total, double difficulty, double accuracy, Double estimatedUnstableRate) {}

  record TaikoTestResult(DifficultyAttributes difficulty, PerformanceAttributes performance) {}

  private static TaikoTestResult runOsuTaiko(List<String> mods, ScoreInfo scoreInfo)
      throws Exception {
    try (Beatmap beatmap = Beatmap.fromFile(File.resourceToTempFile(BEATMAP_RESOURCE));
        Ruleset ruleset = Ruleset.fromId(1);
        var diffCalculator = DifficultyCalculatorFactory.create(ruleset, beatmap);
        var perfCalculator = PerformanceCalculatorFactory.create(ruleset);
        ModsCollection modsCollection = ModsCollection.create()) {
      for (String mod : mods) {
        modsCollection.add(Mod.create(mod));
      }

      var diff = diffCalculator.calculate(modsCollection);
      var perf = perfCalculator.calculate(ruleset, beatmap, modsCollection, scoreInfo, diff);

      return new TaikoTestResult(diff, perf);
    }
  }

  private static ScoreInfo someScore() {
    ScoreInfo score = new ScoreInfo();
    score.setAccuracy(1.0);
    score.setMaxCombo(453);
    score.setCountGreat(453);
    return score;
  }
}
