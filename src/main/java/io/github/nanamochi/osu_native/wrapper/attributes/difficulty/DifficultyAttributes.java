package io.github.nanamochi.osu_native.wrapper.attributes.difficulty;

import java.lang.foreign.MemorySegment;
import lombok.Data;

@Data
public abstract class DifficultyAttributes {
  private MemorySegment nativeSegment;
  private double starRating;
  private int maxCombo;
}
