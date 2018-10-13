package taximeter.adapter.strategy;

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

  public PeakSettings(LocalTime begin, LocalTime end, BigDecimal ratio) {
    this(new PeakPeriod(begin, end), ratio);
  }

  public boolean isPeakTime(LocalTime time) {
    return peakPeriod.isPeakTime(time);
  }
}
