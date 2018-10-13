package taximeter.adapter.strategy;

import com.google.common.collect.Range;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@EqualsAndHashCode
public class PeakPeriod {

  private Range<LocalTime> period;

  /** 左闭右开 */
  public PeakPeriod(LocalTime begin, LocalTime end) {
    this.period = Range.closedOpen(begin, end);
  }

  /**
   * 两个period是否相交(有重复区间)
   *
   * @param otherPeakPeriod
   * @return
   */
  public boolean isIntersected(PeakPeriod otherPeakPeriod) {
    Range<LocalTime> otherPeriod = otherPeakPeriod.period;
    return this.period.isConnected(otherPeriod)
        && !(this.period.upperEndpoint().equals(otherPeriod.lowerEndpoint())
            || this.period.lowerEndpoint().equals(otherPeriod.upperEndpoint()));
  }

  public boolean isPeakTime(LocalTime time) {
    return this.period.contains(time);
  }

  @Override
  public String toString() {
    return period.toString();
  }
}
