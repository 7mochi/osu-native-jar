package io.github.nanamochi.osu_native.wrapper.attributes.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManiaPerformanceAttributes extends PerformanceAttributes {
  private double difficulty;

  public ManiaPerformanceAttributes(double total, double difficulty) {
    super(total);
    this.difficulty = difficulty;
  }
}
