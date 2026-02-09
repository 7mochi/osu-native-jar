package io.github.nanamochi.osu_native.wrapper.attributes.difficulty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OsuDifficultyAttributes extends DifficultyAttributes {
  private double aimDifficulty;
  private double aimDifficultSliderCount;
  private double speedDifficulty;
  private double speedNoteCount;
  private double flashlightDifficulty;
  private double sliderFactor;
  private double aimTopWeightedSliderFactor;
  private double speedTopWeightedSliderFactor;
  private double aimDifficultStrainCount;
  private double speedDifficultStrainCount;
  private double nestedScorePerObject;
  private double legacyScoreBaseMultiplier;
  private double maximumLegacyComboScore;
  private double drainRate;
  private int hitCircleCount;
  private int sliderCount;
  private int spinnerCount;
}
