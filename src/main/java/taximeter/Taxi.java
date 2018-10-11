package taximeter;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author yanweijin
 * @date 2018/10/9
 */
public class Taxi {

	private Locale locale;

	private Taximeter taximeter;

	public Taxi(Locale locale) {
		this.locale = locale;
		this.taximeter = new DefaultTaximeter();
	}

	public Bill run(TaxiTime boardingTime, BillingStrategy billingStrategy, Integer distance) {
		BillingTrip trip = taximeter.start(boardingTime, billingStrategy);
		for (int i = 0; i < distance; i++) {
			trip.increaseDistance(boardingTime);
		}
		return trip.checkout();
	}

	public Bill run(TaxiTime boardingTime, Integer distance) {
		return run(boardingTime, getBillingStrategy(boardingTime), distance);
	}

	public BillingTrip start(TaxiTime boardingTime, BillingStrategy billingStrategy) {
		return taximeter.start(boardingTime, billingStrategy);
	}

	public BillingTrip start(TaxiTime boardingTime) {
		return start(boardingTime, getBillingStrategy(boardingTime));
	}

	private BillingStrategy getBillingStrategy(TaxiTime boardingTime) {
		TimeRange timeRange = boardingTime.getTimeRange();
		if (timeRange == TimeRange.DAY) {
			return new DefaultDayBillingStrategy();
		} else if (timeRange == TimeRange.NIGHT) {
			return new DefaultNightBillingStrategy();
		}
		throw new IllegalArgumentException("无效的时间范围: " + timeRange);
	}

	private class DefaultDayBillingStrategy implements BillingStrategy {
		private int paidDistance = 3;

		@Override
		public BigDecimal getStartingPrice(TaxiTime currentTime) {
			return new BigDecimal(14);
		}

		@Override
		public BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime) {
			if (currentDistance > 10 && locale == Locale.INNER_RING) {
				return new BigDecimal(3.5);
			} else if (currentDistance > paidDistance) {
				return new BigDecimal(2.5);
			}
			return BigDecimal.ZERO;
		}
	}

	@Getter
	private class DefaultNightBillingStrategy implements BillingStrategy {
		private int paidDistance = 3;

		@Override
		public BigDecimal getStartingPrice(TaxiTime currentTime) {
			return new BigDecimal(18);
		}

		@Override
		public BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime) {
			if (currentDistance > 10 && locale == Locale.INNER_RING) {
				return new BigDecimal(4.7);
			} else if (currentDistance > paidDistance) {
				return new BigDecimal(3);
			}
			return BigDecimal.ZERO;
		}
	}

}
