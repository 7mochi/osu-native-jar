package io.github.nanamochi.osu_native.wrapper.calculators.difficulty;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.OsuDifficultyCalculator_Calculate;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.OsuDifficultyCalculator_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.OsuDifficultyCalculator_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeOsuDifficultyAttributes;
import io.github.nanamochi.osu_native.bindings.NativeOsuDifficultyCalculator;
import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.OsuDifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class OsuDifficultyCalculator extends DifficultyCalculator<OsuDifficultyAttributes> {
  private OsuDifficultyCalculator(MemorySegment handle, Arena arena) {
    super(handle, arena);
  }

  public static OsuDifficultyCalculator create(Ruleset ruleset, Beatmap beatmap) {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeOsuDifficultyCalculator =
        arena.allocate(NativeOsuDifficultyCalculator.layout());
    int result =
        OsuDifficultyCalculator_Create(
            ruleset.getHandle(), beatmap.getHandle(), nativeOsuDifficultyCalculator);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create OsuDifficultyCalculator. Error: " + ErrorCode.fromValue(result));
    }

    return new OsuDifficultyCalculator(nativeOsuDifficultyCalculator, arena);
  }

  @Override
  public OsuDifficultyAttributes calculate(ModsCollection mods) {
    MemorySegment nativeOsuDifficultyAttributes =
        arena.allocate(NativeOsuDifficultyAttributes.layout());
    int result =
        OsuDifficultyCalculator_Calculate(
            this.handle, mods.getHandle(), nativeOsuDifficultyAttributes);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to calculate OsuDifficultyAttributes with mods. Error: "
              + ErrorCode.fromValue(result));
    }

    return fillFromNative(new OsuDifficultyAttributes(), nativeOsuDifficultyAttributes);
  }

  private OsuDifficultyAttributes fillFromNative(
      OsuDifficultyAttributes out, MemorySegment nativeSeg) {
    out.setStarRating(NativeOsuDifficultyAttributes.starRating(nativeSeg));
    out.setMaxCombo(NativeOsuDifficultyAttributes.maxCombo(nativeSeg));
    out.setAimDifficulty(NativeOsuDifficultyAttributes.aimDifficulty(nativeSeg));
    out.setAimDifficultSliderCount(
        NativeOsuDifficultyAttributes.aimDifficultSliderCount(nativeSeg));
    out.setSpeedDifficulty(NativeOsuDifficultyAttributes.speedDifficulty(nativeSeg));
    out.setSpeedNoteCount(NativeOsuDifficultyAttributes.speedNoteCount(nativeSeg));
    out.setFlashlightDifficulty(NativeOsuDifficultyAttributes.flashlightDifficulty(nativeSeg));
    out.setSliderFactor(NativeOsuDifficultyAttributes.sliderFactor(nativeSeg));
    out.setAimTopWeightedSliderFactor(
        NativeOsuDifficultyAttributes.aimTopWeightedSliderFactor(nativeSeg));
    out.setSpeedTopWeightedSliderFactor(
        NativeOsuDifficultyAttributes.speedTopWeightedSliderFactor(nativeSeg));
    out.setAimDifficultStrainCount(
        NativeOsuDifficultyAttributes.aimDifficultStrainCount(nativeSeg));
    out.setSpeedDifficultStrainCount(
        NativeOsuDifficultyAttributes.speedDifficultStrainCount(nativeSeg));
    out.setNestedScorePerObject(NativeOsuDifficultyAttributes.nestedScorePerObject(nativeSeg));
    out.setLegacyScoreBaseMultiplier(
        NativeOsuDifficultyAttributes.legacyScoreBaseMultiplier(nativeSeg));
    out.setMaximumLegacyComboScore(
        NativeOsuDifficultyAttributes.maximumLegacyComboScore(nativeSeg));
    out.setDrainRate(NativeOsuDifficultyAttributes.drainRate(nativeSeg));
    out.setHitCircleCount(NativeOsuDifficultyAttributes.hitCircleCount(nativeSeg));
    out.setSliderCount(NativeOsuDifficultyAttributes.sliderCount(nativeSeg));
    out.setSpinnerCount(NativeOsuDifficultyAttributes.spinnerCount(nativeSeg));
    out.setNativeSegment(nativeSeg);
    return out;
  }

  @Override
  protected void destroyNative(MemorySegment handle) {
    OsuDifficultyCalculator_Destroy(handle);
  }
}
