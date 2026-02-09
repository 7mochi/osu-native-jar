package io.github.nanamochi.osu_native.wrapper.calculators.difficulty;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.CatchDifficultyCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.CatchDifficultyCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.CatchDifficultyCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeCatchDifficultyAttributes;
import io.github.nanamochi.osu_native.bindings.NativeCatchDifficultyCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.CatchDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.Beatmap;
import io.github.nanamochi.osu_native.wrapper.objects.ErrorCode;
import io.github.nanamochi.osu_native.wrapper.objects.ModsCollection;
import io.github.nanamochi.osu_native.wrapper.objects.Ruleset;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class CatchDifficultyCalculator extends DifficultyCalculator<CatchDifficultyAttributes> {
  private CatchDifficultyCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static CatchDifficultyCalculator create(Ruleset ruleset, Beatmap beatmap) {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeCatchDifficultyCalculator =
        arena.allocate(NativeCatchDifficultyCalculator.layout());
    int result =
        CatchDifficultyCalculator_Create(
            ruleset.getHandle(), beatmap.getHandle(), nativeCatchDifficultyCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create CatchDifficultyCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new CatchDifficultyCalculator(nativeCatchDifficultyCalculator, arena);
  }

  @Override
  public CatchDifficultyAttributes calculate(ModsCollection mods) {
    MemorySegment nativeCatchDifficultyAttributes =
        arena.allocate(NativeCatchDifficultyAttributes.layout());
    int result =
        CatchDifficultyCalculator_Calculate(
            this.handle, mods.getHandle(), nativeCatchDifficultyAttributes);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate CatchDifficultyAttributes with mods. Error: "
              + ErrorCode.fromValue(result));
    }

    return fillFromNative(new CatchDifficultyAttributes(), nativeCatchDifficultyAttributes);
  }

  private CatchDifficultyAttributes fillFromNative(
      CatchDifficultyAttributes out, MemorySegment nativeSeg) {
    out.setStarRating(NativeCatchDifficultyAttributes.starRating(nativeSeg));
    out.setMaxCombo(NativeCatchDifficultyAttributes.maxCombo(nativeSeg));
    out.setNativeSegment(nativeSeg);
    return out;
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    CatchDifficultyCalculator_Destroy(handle);
  }
}
