package taximeter.adapter.strategy;

import com.google.common.collect.Range;
import lombok.Getter;
import taximeter.domain.TimeRange;

import java.math.BigDecimal;

/**
 * @author yanweijin
 * @date 2018/10/13
 */
@Getter
public class PriceSettings {
	private TimeRange timeRange;
	private Range<Integer> distanceRange;
	private BigDecimal price;

	public PriceSettings(TimeRange timeRange, int distanceBegin, int distanceEnd, BigDecimal price) {
		if (distanceBegin < 0 || distanceEnd < 0 || distanceBegin < distanceEnd) {
			throw new IllegalArgumentException("路程范围配置不正确:distanceBegin=" + distanceBegin + ",distanceEnd=" + distanceEnd);
		}
		this.distanceRange = Range.closedOpen(distanceBegin, distanceEnd);
		this.timeRange = timeRange;
		this.price = price;
	}

}
