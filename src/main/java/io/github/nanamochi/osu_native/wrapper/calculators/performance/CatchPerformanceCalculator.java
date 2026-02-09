package io.github.nanamochi.osu_native.wrapper.calculators.performance;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.CatchPerformanceCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.CatchPerformanceCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.CatchPerformanceCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeCatchPerformanceAttributes;
import io.github.nanamochi.osu_native.bindings.NativeCatchPerformanceCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.CatchPerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class CatchPerformanceCalculator extends PerformanceCalculator {
  private CatchPerformanceCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static CatchPerformanceCalculator create() {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeCatchPerformanceCalculator =
        arena.allocate(NativeCatchPerformanceCalculator.layout());
    int result = CatchPerformanceCalculator_Create(nativeCatchPerformanceCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create CatchPerformanceCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new CatchPerformanceCalculator(nativeCatchPerformanceCalculator, arena);
  }

  @Override
  public CatchPerformanceAttributes calculate(
      Ruleset ruleset,
      Beatmap beatmap,
      ModsCollection mods,
      ScoreInfo scoreInfo,
      DifficultyAttributes difficultyAttributes) {
    MemorySegment nativeCatchPerformanceAttributesOutput =
        arena.allocate(NativeCatchPerformanceAttributes.layout());

    if (ruleset != null) scoreInfo.setRulesetHandle(ruleset.getHandle());
    if (beatmap != null) scoreInfo.setBeatmapHandle(beatmap.getHandle());
    if (mods != null) scoreInfo.setModsHandle(mods.getHandle());

    MemorySegment nativeScoreInfo = scoreInfo.convertToNative(arena);

    int result =
        CatchPerformanceCalculator_Calculate(
            this.handle,
            nativeScoreInfo,
            difficultyAttributes.getNativeSegment(),
            nativeCatchPerformanceAttributesOutput);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate performance. Error: " + ErrorCode.fromValue(result));
    }
    double total = NativeCatchPerformanceAttributes.total(nativeCatchPerformanceAttributesOutput);

    return new CatchPerformanceAttributes(total);
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    CatchPerformanceCalculator_Destroy(handle);
  }
}
