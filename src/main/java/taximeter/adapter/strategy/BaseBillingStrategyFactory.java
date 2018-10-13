package taximeter.adapter.strategy;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import taximeter.domain.*;

import java.math.BigDecimal;
import java.util.Map;

import static java.math.BigDecimal.valueOf;

/**
 * @author yanweijin
 * @date 2018/10/13
 */
public class BaseBillingStrategyFactory implements BillingStrategyFactory {

	private Map<Pair, BaseBillingStrategy> strategyMapping;

	@Setter
	private PeakBillingStrategyFactory peakBillingStrategyFactory;

	public BaseBillingStrategyFactory() {
		strategyMapping = Maps.newHashMapWithExpectedSize(4);
		strategyMapping.put(Pair.of(Locale.INNER_RING, TimeRange.DAY),
				BaseBillingStrategy.defaultBuilder()
						.startingPrice(valueOf(14))
						.shortDistancePrice(valueOf(2.5))
						.longDistancePrice(valueOf(3.5))
						.build());
		strategyMapping.put(Pair.of(Locale.INNER_RING, TimeRange.NIGHT),
				BaseBillingStrategy.defaultBuilder()
						.startingPrice(valueOf(18))
						.shortDistancePrice(valueOf(3))
						.longDistancePrice(valueOf(4.7))
						.build());
		strategyMapping.put(Pair.of(Locale.OUTER_RING, TimeRange.DAY),
				BaseBillingStrategy.defaultBuilder()
						.startingPrice(valueOf(14))
						.shortDistancePrice(valueOf(2.5))
						.longDistancePrice(valueOf(2.5))
						.build());
		strategyMapping.put(Pair.of(Locale.OUTER_RING, TimeRange.NIGHT),
				BaseBillingStrategy.defaultBuilder()
						.startingPrice(valueOf(18))
						.shortDistancePrice(valueOf(3))
						.longDistancePrice(valueOf(3))
						.build());
	}


	@Override
	public BillingStrategy getBillingStrategy(Taxi taxi, TaxiTime currentTime) {
		Locale locale = taxi.getLocale();
		TimeRange timeRange = currentTime.getTimeRange();
		BaseBillingStrategy baseBillingStrategy = strategyMapping.get(Pair.of(locale, timeRange));
		if (peakBillingStrategyFactory != null) {
			return peakBillingStrategyFactory.getPeakBillingStrategy(baseBillingStrategy);
		} else {
			return baseBillingStrategy;
		}
	}


	@Value(staticConstructor = "of")
	private static class Pair {
		private Locale locale;
		private TimeRange timeRange;
	}

	/**
	 * @author yanweijin
	 * @date 2018/10/13
	 */
	@Builder
	@AllArgsConstructor
	private static class BaseBillingStrategy implements BillingStrategy {
		private int paidDistance;
		private int longDistanceCriticalPoint;
		private BigDecimal startingPrice;
		private BigDecimal shortDistancePrice;
		private BigDecimal longDistancePrice;

		public static BaseBillingStrategyBuilder defaultBuilder(){
			return new BaseBillingStrategyBuilder().paidDistance(3).longDistanceCriticalPoint(10);
		}

		@Override
		public BigDecimal getStartingPrice(TaxiTime currentTime) {
			return startingPrice;
		}

		@Override
		public BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime) {
			if (currentDistance > longDistanceCriticalPoint) {
				return longDistancePrice;
			} else if (currentDistance > paidDistance) {
				return shortDistancePrice;
			}
			return BigDecimal.ZERO;
		}

	}
}
