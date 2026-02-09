package io.github.nanamochi.osu_native.wrapper.attributes.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaikoPerformanceAttributes extends PerformanceAttributes {
  private double difficulty;
  private double accuracy;
  private Double estimatedUnstableRate;

  public TaikoPerformanceAttributes(
      double total, double difficulty, double accuracy, Double estimatedUnstableRate) {
    super(total);
    this.difficulty = difficulty;
    this.accuracy = accuracy;
    this.estimatedUnstableRate = estimatedUnstableRate;
  }
}
