package io.github.nanamochi.osu_native;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.nanamochi.osu_native.utils.File;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.CatchDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.CatchPerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.PerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.factories.DifficultyCalculatorFactory;
import io.github.nanamochi.osu_native.wrapper.factories.PerformanceCalculatorFactory;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Disabled // TODO: Disabled for now because there is a bug on the osu-native side that causes
// incorrect pp calculation
@DisplayName("osu-native-jar – Catch ruleset tests")
public class CatchRulesetTests {
  private static final String BEATMAP_RESOURCE = "4289411.osu";

  @Nested
  @DisplayName("Difficulty & performance attributes")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class OsuCatchAttributeTests {
    Stream<Arguments> cases() {
      return Stream.of(
          Arguments.of(
              List.of(),
              fullComboScore(),
              new ExpectedDifficulty(8.023384332389398, 1909),
              new ExpectedPerformance(853)));
    }

    @ParameterizedTest(name = "Difficulty attributes for mods = {0}")
    @MethodSource("cases")
    void testDifficultyAttributes(
        List<String> mods,
        ScoreInfo score,
        ExpectedDifficulty expected,
        ExpectedPerformance ignored)
        throws Exception {
      CatchDifficultyAttributes diff =
          (CatchDifficultyAttributes) runOsuCatch(mods, score).difficulty();
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
      CatchPerformanceAttributes perf =
          (CatchPerformanceAttributes) runOsuCatch(mods, score).performance();
      assertAll(() -> assertEquals(expected.total(), perf.getTotal()));
    }
  }

  record ExpectedDifficulty(double starRating, int maxCombo) {}

  record ExpectedPerformance(double total) {}

  record CatchTestResult(DifficultyAttributes difficulty, PerformanceAttributes performance) {}

  private static CatchTestResult runOsuCatch(List<String> mods, ScoreInfo scoreInfo)
      throws Exception {
    try (Beatmap beatmap = Beatmap.fromFile(File.resourceToTempFile(BEATMAP_RESOURCE));
        Ruleset ruleset = Ruleset.fromId(2);
        var diffCalculator = DifficultyCalculatorFactory.create(ruleset, beatmap);
        var perfCalculator = PerformanceCalculatorFactory.create(ruleset);
        ModsCollection modsCollection = ModsCollection.create()) {
      for (String mod : mods) {
        modsCollection.add(Mod.create(mod));
      }

      var diff = diffCalculator.calculate(modsCollection);
      var perf = perfCalculator.calculate(ruleset, beatmap, modsCollection, scoreInfo, diff);

      return new CatchTestResult(diff, perf);
    }
  }

  private static ScoreInfo fullComboScore() {
    ScoreInfo score = new ScoreInfo();
    // TODO: setters for catch calculation are missing in osu-native
    // score.setAccuracy(1.0);
    // score.setMaxCombo(1909);
    // score.setCountGreat(1836);
    // score.setCountOk(73);
    // score.setCountMeh(86);
    return score;
  }
}
