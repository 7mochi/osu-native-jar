package io.github.nanamochi.osu_native.wrapper.attributes.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OsuPerformanceAttributes extends PerformanceAttributes {
  private double aim;
  private double speed;
  private double accuracy;
  private double flashlight;
  private double effectiveMissCount;
  private Double speedDeviation;
  private double comboBasedEstimatedMissCount;
  private Double scoreBasedEstimatedMissCount;
  private double aimEstimatedSliderBreaks;
  private double speedEstimatedSliderBreaks;

  public OsuPerformanceAttributes(
      double total,
      double aim,
      double speed,
      double accuracy,
      double flashlight,
      double effectiveMissCount,
      Double speedDeviation,
      double comboBasedEstimatedMissCount,
      Double scoreBasedEstimatedMissCount,
      double aimEstimatedSliderBreaks,
      double speedEstimatedSliderBreaks) {
    super(total);
    this.aim = aim;
    this.speed = speed;
    this.accuracy = accuracy;
    this.flashlight = flashlight;
    this.effectiveMissCount = effectiveMissCount;
    this.speedDeviation = speedDeviation;
    this.comboBasedEstimatedMissCount = comboBasedEstimatedMissCount;
    this.scoreBasedEstimatedMissCount = scoreBasedEstimatedMissCount;
    this.aimEstimatedSliderBreaks = aimEstimatedSliderBreaks;
    this.speedEstimatedSliderBreaks = speedEstimatedSliderBreaks;
  }
}
