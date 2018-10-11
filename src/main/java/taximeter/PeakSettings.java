package taximeter;

import com.google.common.collect.Range;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

public class PeakSettings {
  private Range<LocalTime> peakTime;
  @Getter
  private BigDecimal ratio;

  public PeakSettings(Range<LocalTime> peakTime, BigDecimal ratio) {
    this.peakTime = peakTime;
    this.ratio = ratio;
  }

  public boolean isPeakTime(LocalTime time) {
    return peakTime.contains(time);
  }
}
