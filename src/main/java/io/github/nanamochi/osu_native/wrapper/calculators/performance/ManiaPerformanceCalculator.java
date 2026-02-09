package io.github.nanamochi.osu_native.wrapper.calculators.performance;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.ManiaPerformanceCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ManiaPerformanceCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ManiaPerformanceCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeManiaPerformanceAttributes;
import io.github.nanamochi.osu_native.bindings.NativeManiaPerformanceCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.ManiaPerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class ManiaPerformanceCalculator extends PerformanceCalculator {
  private ManiaPerformanceCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static ManiaPerformanceCalculator create() {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeManiaPerformanceCalculator =
        arena.allocate(NativeManiaPerformanceCalculator.layout());
    int result = ManiaPerformanceCalculator_Create(nativeManiaPerformanceCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create ManiaPerformanceCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new ManiaPerformanceCalculator(nativeManiaPerformanceCalculator, arena);
  }

  @Override
  public ManiaPerformanceAttributes calculate(
      Ruleset ruleset,
      Beatmap beatmap,
      ModsCollection mods,
      ScoreInfo scoreInfo,
      DifficultyAttributes difficultyAttributes) {
    MemorySegment nativeManiaPerformanceAttributesOutput =
        arena.allocate(NativeManiaPerformanceAttributes.layout());

    if (ruleset != null) scoreInfo.setRulesetHandle(ruleset.getHandle());
    if (beatmap != null) scoreInfo.setBeatmapHandle(beatmap.getHandle());
    if (mods != null) scoreInfo.setModsHandle(mods.getHandle());

    MemorySegment nativeScoreInfo = scoreInfo.convertToNative(arena);

    int result =
        ManiaPerformanceCalculator_Calculate(
            this.handle,
            nativeScoreInfo,
            difficultyAttributes.getNativeSegment(),
            nativeManiaPerformanceAttributesOutput);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate performance. Error: " + ErrorCode.fromValue(result));
    }
    double total = NativeManiaPerformanceAttributes.total(nativeManiaPerformanceAttributesOutput);
    double difficulty =
        NativeManiaPerformanceAttributes.difficulty(nativeManiaPerformanceAttributesOutput);

    return new ManiaPerformanceAttributes(total, difficulty);
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    ManiaPerformanceCalculator_Destroy(handle);
  }
}
