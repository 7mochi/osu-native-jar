package io.github.nanamochi.osu_native.wrapper.calculators.difficulty;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.TaikoDifficultyCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.TaikoDifficultyCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.TaikoDifficultyCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeTaikoDifficultyAttributes;
import io.github.nanamochi.osu_native.bindings.NativeTaikoDifficultyCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.TaikoDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.Beatmap;
import io.github.nanamochi.osu_native.wrapper.objects.ErrorCode;
import io.github.nanamochi.osu_native.wrapper.objects.ModsCollection;
import io.github.nanamochi.osu_native.wrapper.objects.Ruleset;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class TaikoDifficultyCalculator extends DifficultyCalculator<TaikoDifficultyAttributes> {
  private TaikoDifficultyCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static TaikoDifficultyCalculator create(Ruleset ruleset, Beatmap beatmap) {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeTaikoDifficultyCalculator =
        arena.allocate(NativeTaikoDifficultyCalculator.layout());
    int result =
        TaikoDifficultyCalculator_Create(
            ruleset.getHandle(), beatmap.getHandle(), nativeTaikoDifficultyCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create TaikoDifficultyCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new TaikoDifficultyCalculator(nativeTaikoDifficultyCalculator, arena);
  }

  @Override
  public TaikoDifficultyAttributes calculate(ModsCollection mods) {
    MemorySegment nativeTaikoDifficultyAttributes =
        arena.allocate(NativeTaikoDifficultyAttributes.layout());
    int result =
        TaikoDifficultyCalculator_Calculate(
            this.handle, mods.getHandle(), nativeTaikoDifficultyAttributes);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate TaikoDifficultyAttributes with mods. Error: "
              + ErrorCode.fromValue(result));
    }

    return fillFromNative(new TaikoDifficultyAttributes(), nativeTaikoDifficultyAttributes);
  }

  private TaikoDifficultyAttributes fillFromNative(
      TaikoDifficultyAttributes out, MemorySegment nativeSeg) {
    out.setStarRating(NativeTaikoDifficultyAttributes.starRating(nativeSeg));
    out.setMaxCombo(NativeTaikoDifficultyAttributes.maxCombo(nativeSeg));
    out.setMechanicalDifficulty(NativeTaikoDifficultyAttributes.mechanicalDifficulty(nativeSeg));
    out.setRhythmDifficulty(NativeTaikoDifficultyAttributes.rhythmDifficulty(nativeSeg));
    out.setReadingDifficulty(NativeTaikoDifficultyAttributes.readingDifficulty(nativeSeg));
    out.setColourDifficulty(NativeTaikoDifficultyAttributes.colourDifficulty(nativeSeg));
    out.setStaminaDifficulty(NativeTaikoDifficultyAttributes.staminaDifficulty(nativeSeg));
    out.setMonoStaminaFactor(NativeTaikoDifficultyAttributes.monoStaminaFactor(nativeSeg));
    out.setConsistencyFactor(NativeTaikoDifficultyAttributes.consistencyFactor(nativeSeg));
    out.setStaminaTopStrains(NativeTaikoDifficultyAttributes.staminaTopStrains(nativeSeg));
    out.setNativeSegment(nativeSeg);
    return out;
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    TaikoDifficultyCalculator_Destroy(handle);
  }
}
