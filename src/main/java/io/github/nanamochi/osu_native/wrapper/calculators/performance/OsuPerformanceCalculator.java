package io.github.nanamochi.osu_native.wrapper.calculators.performance;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.OsuPerformanceCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.OsuPerformanceCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.OsuPerformanceCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.Cabinet__Nullable_double;
import io.github.nanamochi.osu_native.bindings.NativeOsuPerformanceAttributes;
import io.github.nanamochi.osu_native.bindings.NativeOsuPerformanceCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.OsuPerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class OsuPerformanceCalculator extends PerformanceCalculator {
  private OsuPerformanceCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static OsuPerformanceCalculator create() {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeOsuPerformanceCalculator =
        arena.allocate(NativeOsuPerformanceCalculator.layout());
    int result = OsuPerformanceCalculator_Create(nativeOsuPerformanceCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create OsuPerformanceCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new OsuPerformanceCalculator(nativeOsuPerformanceCalculator, arena);
  }

  @Override
  public OsuPerformanceAttributes calculate(
      Ruleset ruleset,
      Beatmap beatmap,
      ModsCollection mods,
      ScoreInfo scoreInfo,
      DifficultyAttributes difficultyAttributes) {
    MemorySegment nativeOsuPerformanceAttributesOutput =
        arena.allocate(NativeOsuPerformanceAttributes.layout());

    if (ruleset != null) scoreInfo.setRulesetHandle(ruleset.getHandle());
    if (beatmap != null) scoreInfo.setBeatmapHandle(beatmap.getHandle());
    if (mods != null) scoreInfo.setModsHandle(mods.getHandle());

    MemorySegment nativeScoreInfo = scoreInfo.convertToNative(arena);

    int result =
        OsuPerformanceCalculator_Calculate(
            this.handle,
            nativeScoreInfo,
            difficultyAttributes.getNativeSegment(),
            nativeOsuPerformanceAttributesOutput);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate performance. Error: " + ErrorCode.fromValue(result));
    }

    double total = NativeOsuPerformanceAttributes.total(nativeOsuPerformanceAttributesOutput);
    double aim = NativeOsuPerformanceAttributes.aim(nativeOsuPerformanceAttributesOutput);
    double speed = NativeOsuPerformanceAttributes.speed(nativeOsuPerformanceAttributesOutput);
    double accuracy = NativeOsuPerformanceAttributes.accuracy(nativeOsuPerformanceAttributesOutput);
    double flashlight =
        NativeOsuPerformanceAttributes.flashlight(nativeOsuPerformanceAttributesOutput);
    double effectiveMissCount =
        NativeOsuPerformanceAttributes.effectiveMissCount(nativeOsuPerformanceAttributesOutput);

    MemorySegment speedDeviationSegment =
        NativeOsuPerformanceAttributes.speedDeviation(nativeOsuPerformanceAttributesOutput);
    Double speedDeviation =
        Cabinet__Nullable_double.hasValue(speedDeviationSegment)
            ? Cabinet__Nullable_double.value(speedDeviationSegment)
            : null;

    double comboBasedEstimatedMissCount =
        NativeOsuPerformanceAttributes.comboBasedEstimatedMissCount(
            nativeOsuPerformanceAttributesOutput);

    MemorySegment scoreBasedEstimatedMissCountSegment =
        NativeOsuPerformanceAttributes.scoreBasedEstimatedMissCount(
            nativeOsuPerformanceAttributesOutput);
    Double scoreBasedEstimatedMissCount =
        Cabinet__Nullable_double.hasValue(scoreBasedEstimatedMissCountSegment)
            ? Cabinet__Nullable_double.value(scoreBasedEstimatedMissCountSegment)
            : null;

    double aimEstimatedSliderBreaks =
        NativeOsuPerformanceAttributes.aimEstimatedSliderBreaks(
            nativeOsuPerformanceAttributesOutput);
    double speedEstimatedSliderBreaks =
        NativeOsuPerformanceAttributes.speedEstimatedSliderBreaks(
            nativeOsuPerformanceAttributesOutput);

    return new OsuPerformanceAttributes(
        total,
        aim,
        speed,
        accuracy,
        flashlight,
        effectiveMissCount,
        speedDeviation,
        comboBasedEstimatedMissCount,
        scoreBasedEstimatedMissCount,
        aimEstimatedSliderBreaks,
        speedEstimatedSliderBreaks);
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    OsuPerformanceCalculator_Destroy(handle);
  }
}
