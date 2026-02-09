package io.github.nanamochi.osu_native.wrapper.attributes.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CatchPerformanceAttributes extends PerformanceAttributes {
  public CatchPerformanceAttributes(double total) {
    super(total);
  }
}
