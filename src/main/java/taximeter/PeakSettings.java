package taximeter;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

public class PeakSettings {
  @Getter
  private PeakPeriod peakPeriod;
  @Getter
  private BigDecimal ratio;

  public PeakSettings(PeakPeriod peakPeriod, BigDecimal ratio) {
    this.peakPeriod = peakPeriod;
    this.ratio = ratio;
  }

  public boolean isPeakTime(LocalTime time) {
    return peakPeriod.isPeakTime(time);
  }
}
