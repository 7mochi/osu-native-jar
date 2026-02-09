package io.github.nanamochi.osu_native.wrapper.attributes.difficulty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaikoDifficultyAttributes extends DifficultyAttributes {
  private double mechanicalDifficulty;
  private double rhythmDifficulty;
  private double readingDifficulty;
  private double colourDifficulty;
  private double staminaDifficulty;
  private double monoStaminaFactor;
  private double consistencyFactor;
  private double staminaTopStrains;
}
