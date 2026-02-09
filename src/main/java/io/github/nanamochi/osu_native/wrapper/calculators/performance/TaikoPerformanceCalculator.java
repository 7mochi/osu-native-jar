package io.github.nanamochi.osu_native.wrapper.calculators.performance;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.TaikoPerformanceCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.TaikoPerformanceCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.TaikoPerformanceCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.Cabinet__Nullable_double;
import io.github.nanamochi.osu_native.bindings.NativeTaikoPerformanceAttributes;
import io.github.nanamochi.osu_native.bindings.NativeTaikoPerformanceCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.TaikoPerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class TaikoPerformanceCalculator extends PerformanceCalculator {
  private TaikoPerformanceCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static TaikoPerformanceCalculator create() {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeTaikoPerformanceCalculator =
        arena.allocate(NativeTaikoPerformanceCalculator.layout());
    int result = TaikoPerformanceCalculator_Create(nativeTaikoPerformanceCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create TaikoPerformanceCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new TaikoPerformanceCalculator(nativeTaikoPerformanceCalculator, arena);
  }

  @Override
  public TaikoPerformanceAttributes calculate(
      Ruleset ruleset,
      Beatmap beatmap,
      ModsCollection mods,
      ScoreInfo scoreInfo,
      DifficultyAttributes difficultyAttributes) {
    MemorySegment nativeTaikoPerformanceAttributesOutput =
        arena.allocate(NativeTaikoPerformanceAttributes.layout());

    if (ruleset != null) scoreInfo.setRulesetHandle(ruleset.getHandle());
    if (beatmap != null) scoreInfo.setBeatmapHandle(beatmap.getHandle());
    if (mods != null) scoreInfo.setModsHandle(mods.getHandle());

    MemorySegment nativeScoreInfo = scoreInfo.convertToNative(arena);

    int result =
        TaikoPerformanceCalculator_Calculate(
            this.handle,
            nativeScoreInfo,
            difficultyAttributes.getNativeSegment(),
            nativeTaikoPerformanceAttributesOutput);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate performance. Error: " + ErrorCode.fromValue(result));
    }
    double total = NativeTaikoPerformanceAttributes.total(nativeTaikoPerformanceAttributesOutput);
    double difficulty =
        NativeTaikoPerformanceAttributes.difficulty(nativeTaikoPerformanceAttributesOutput);
    double accuracy =
        NativeTaikoPerformanceAttributes.accuracy(nativeTaikoPerformanceAttributesOutput);

    MemorySegment estimatedUnstableRateSegment =
        NativeTaikoPerformanceAttributes.estimatedUnstableRate(
            nativeTaikoPerformanceAttributesOutput);
    Double estimatedUnstableRate =
        Cabinet__Nullable_double.hasValue(estimatedUnstableRateSegment)
            ? Cabinet__Nullable_double.value(estimatedUnstableRateSegment)
            : null;

    return new TaikoPerformanceAttributes(total, difficulty, accuracy, estimatedUnstableRate);
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    TaikoPerformanceCalculator_Destroy(handle);
  }
}
