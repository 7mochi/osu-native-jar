package io.github.nanamochi.osu_native.wrapper.calculators.difficulty;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.ManiaDifficultyCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ManiaDifficultyCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ManiaDifficultyCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeCatchDifficultyAttributes;
import io.github.nanamochi.osu_native.bindings.NativeManiaDifficultyAttributes;
import io.github.nanamochi.osu_native.bindings.NativeManiaDifficultyCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.ManiaDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.Beatmap;
import io.github.nanamochi.osu_native.wrapper.objects.ErrorCode;
import io.github.nanamochi.osu_native.wrapper.objects.ModsCollection;
import io.github.nanamochi.osu_native.wrapper.objects.Ruleset;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class ManiaDifficultyCalculator extends DifficultyCalculator<ManiaDifficultyAttributes> {
  private ManiaDifficultyCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static ManiaDifficultyCalculator create(Ruleset ruleset, Beatmap beatmap) {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeManiaDifficultyCalculator =
        arena.allocate(NativeManiaDifficultyCalculator.layout());
    int result =
        ManiaDifficultyCalculator_Create(
            ruleset.getHandle(), beatmap.getHandle(), nativeManiaDifficultyCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create CatchDifficultyCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new ManiaDifficultyCalculator(nativeManiaDifficultyCalculator, arena);
  }

  @Override
  public ManiaDifficultyAttributes calculate(ModsCollection mods) {
    MemorySegment nativeManiaDifficultyAttributes =
        arena.allocate(NativeManiaDifficultyAttributes.layout());
    int result =
        ManiaDifficultyCalculator_Calculate(
            this.handle, mods.getHandle(), nativeManiaDifficultyAttributes);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate CatchDifficultyAttributes with mods. Error: "
              + ErrorCode.fromValue(result));
    }

    return fillFromNative(new ManiaDifficultyAttributes(), nativeManiaDifficultyAttributes);
  }

  private ManiaDifficultyAttributes fillFromNative(
      ManiaDifficultyAttributes out, MemorySegment nativeSeg) {
    out.setStarRating(NativeCatchDifficultyAttributes.starRating(nativeSeg));
    out.setMaxCombo(NativeCatchDifficultyAttributes.maxCombo(nativeSeg));
    out.setNativeSegment(nativeSeg);
    return out;
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    ManiaDifficultyCalculator_Destroy(handle);
  }
}
