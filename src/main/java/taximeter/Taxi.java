package taximeter;

import lombok.Getter;


public class Taxi {

	@Getter
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
			return new DefaultDayBillingStrategy(locale);
		} else if (timeRange == TimeRange.NIGHT) {
			return new DefaultNightBillingStrategy(locale);
		}
		throw new IllegalArgumentException("无效的时间范围: " + timeRange);
	}

}
