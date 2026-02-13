package io.github.nanamochi.osu_native;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nanamochi.osu_native.utils.File;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.OsuDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.OsuPerformanceAttributes;
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

@DisplayName("osu-native-jar – Standard ruleset tests")
public class StandardRulesetTests {
  private static final String BEATMAP_RESOURCE = "5438072.osu";
  private static final double EPSILON = 1e-6;

  @Nested
  @DisplayName("Difficulty & performance attributes")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class OsuStandardAttributeTests {
    Stream<Arguments> cases() {
      return Stream.of(
          Arguments.of(
              List.of("HD", "DT"),
              fullComboScore(),
              new ExpectedDifficulty(
                  7.765762362059099,
                  183,
                  4.3682123359710925,
                  22.686766833130047,
                  2.8560898921433444,
                  113.75894687178317,
                  0.9943922184830679,
                  0.3840426059241585,
                  0.4713024554831769,
                  46.199831564028564,
                  60.800851497024794,
                  18.428571428571427,
                  4,
                  615888),
              new ExpectedPerformance(
                  319.18472409411135,
                  563.1769678409995,
                  87.45481790157747,
                  131.2126708522303,
                  0,
                  0,
                  8.46064257633682,
                  0,
                  null,
                  0,
                  0)));
    }

    @ParameterizedTest(name = "Difficulty attributes for mods = {0}")
    @MethodSource("cases")
    void testDifficultyAttributes(
        List<String> mods,
        ScoreInfo score,
        ExpectedDifficulty expected,
        ExpectedPerformance ignored)
        throws Exception {
      OsuDifficultyAttributes diff =
          (OsuDifficultyAttributes) runOsuStandard(mods, score).difficulty();
      assertAll(
          () -> assertEquals(expected.starRating(), diff.getStarRating(), EPSILON),
          () -> assertEquals(expected.maxCombo(), diff.getMaxCombo()),
          () -> assertEquals(expected.aimDifficulty(), diff.getAimDifficulty(), EPSILON),
          () ->
              assertEquals(expected.aimDifficultySliderCount(), diff.getAimDifficultSliderCount(), EPSILON),
          () -> assertEquals(expected.speedDifficulty(), diff.getSpeedDifficulty(), EPSILON),
          () -> assertEquals(expected.speedNoteCount(), diff.getSpeedNoteCount(), EPSILON),
          () -> assertEquals(expected.sliderFactor(), diff.getSliderFactor(), EPSILON),
          () ->
              assertEquals(
                  expected.aimTopWeightedSliderFactor(), diff.getAimTopWeightedSliderFactor(), EPSILON),
          () ->
              assertEquals(
                  expected.speedTopWeightedSliderFactor(), diff.getSpeedTopWeightedSliderFactor(), EPSILON),
          () -> assertEquals(expected.aimDifficultStrainCount(), diff.getAimDifficultStrainCount(), EPSILON),
          () ->
              assertEquals(
                  expected.speedDifficultStrainCount(), diff.getSpeedDifficultStrainCount(), EPSILON),
          () -> assertEquals(expected.nestedScorePerObject(), diff.getNestedScorePerObject(), EPSILON),
          () ->
              assertEquals(
                  expected.legacyScoreBaseMultiplier(), diff.getLegacyScoreBaseMultiplier()),
          () ->
              assertEquals(expected.maximumLegacyComboScore(), diff.getMaximumLegacyComboScore()));
    }

    @ParameterizedTest(name = "Performance attributes for mods = {0}")
    @MethodSource("cases")
    void testPerformanceAttributes(
        List<String> mods,
        ScoreInfo score,
        ExpectedDifficulty ignored,
        ExpectedPerformance expected)
        throws Exception {
      OsuPerformanceAttributes perf =
          (OsuPerformanceAttributes) runOsuStandard(mods, score).performance();
      assertAll(
          () -> assertEquals(expected.aim(), perf.getAim(), EPSILON),
          () -> assertEquals(expected.total(), perf.getTotal(), EPSILON),
          () -> assertEquals(expected.speed(), perf.getSpeed(), EPSILON),
          () -> assertEquals(expected.accuracy(), perf.getAccuracy(), EPSILON),
          () -> assertEquals(expected.flashlight(), perf.getFlashlight(), EPSILON),
          () -> assertEquals(expected.effectiveMissCount(), perf.getEffectiveMissCount(), EPSILON),
          () -> {
            if (expected.speedDeviation() != null) {
              assertEquals(expected.speedDeviation(), perf.getSpeedDeviation(), EPSILON);
            }
          },
          () ->
              assertEquals(
                  expected.comboBasedEstimatedMissCount(), perf.getComboBasedEstimatedMissCount(), EPSILON),
          () -> {
            if (expected.scoreBasedEstimatedMissCount() != null) {
              assertEquals(expected.scoreBasedEstimatedMissCount(), perf.getScoreBasedEstimatedMissCount(), EPSILON);
            }
          },
          () ->
              assertEquals(expected.aimEstimatedSliderBreaks(), perf.getAimEstimatedSliderBreaks(), EPSILON),
          () ->
              assertEquals(
                  expected.speedEstimatedSliderBreaks(), perf.getSpeedEstimatedSliderBreaks(), EPSILON));
    }
  }

  record ExpectedDifficulty(
      double starRating,
      int maxCombo,
      double aimDifficulty,
      double aimDifficultySliderCount,
      double speedDifficulty,
      double speedNoteCount,
      double sliderFactor,
      double aimTopWeightedSliderFactor,
      double speedTopWeightedSliderFactor,
      double aimDifficultStrainCount,
      double speedDifficultStrainCount,
      double nestedScorePerObject,
      int legacyScoreBaseMultiplier,
      int maximumLegacyComboScore) {}

  record ExpectedPerformance(
      double aim,
      double total,
      double speed,
      double accuracy,
      double flashlight,
      double effectiveMissCount,
      Double speedDeviation,
      double comboBasedEstimatedMissCount,
      Double scoreBasedEstimatedMissCount,
      double aimEstimatedSliderBreaks,
      double speedEstimatedSliderBreaks) {}

  record OsuTestResult(DifficultyAttributes difficulty, PerformanceAttributes performance) {}

  private static OsuTestResult runOsuStandard(List<String> mods, ScoreInfo scoreInfo)
      throws Exception {
    try (Beatmap beatmap = Beatmap.fromFile(File.resourceToTempFile(BEATMAP_RESOURCE));
        Ruleset ruleset = Ruleset.fromId(0);
        var diffCalculator = DifficultyCalculatorFactory.create(ruleset, beatmap);
        var perfCalculator = PerformanceCalculatorFactory.create(ruleset);
        ModsCollection modsCollection = ModsCollection.create()) {
      for (String mod : mods) {
        modsCollection.add(Mod.create(mod));
      }

      var diff = diffCalculator.calculate(modsCollection);
      var perf = perfCalculator.calculate(ruleset, beatmap, modsCollection, scoreInfo, diff);

      return new OsuTestResult(diff, perf);
    }
  }

  private static ScoreInfo fullComboScore() {
    ScoreInfo score = new ScoreInfo();
    score.setAccuracy(1.0);
    score.setMaxCombo(183);
    score.setCountGreat(140);
    score.setCountSliderTailHit(43);
    score.setCountMiss(0);
    return score;
  }
}
