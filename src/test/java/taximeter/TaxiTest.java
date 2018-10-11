package taximeter;

import com.google.common.collect.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class TaxiTest {

	@ParameterizedTest
	@CsvSource({
			"INNER_RING,    DAY,    0,      14",
			"INNER_RING,    DAY,    1,      14",
			"INNER_RING,    DAY,    3,      14",
			"INNER_RING,    DAY,    4,      16.5",
			"INNER_RING,    DAY,    10,     31.5",
			"INNER_RING,    DAY,    11,     35",
			"OUTER_RING,    DAY,    10,     31.5",
			"OUTER_RING,    DAY,    11,     34",
			"INNER_RING,    NIGHT,  0,      18",
			"INNER_RING,    NIGHT,  1,      18",
			"INNER_RING,    NIGHT,  3,      18",
			"INNER_RING,    NIGHT,  4,      21",
			"INNER_RING,    NIGHT,  10,     39",
			"INNER_RING,    NIGHT,  11,     43.7",
			"OUTER_RING,    NIGHT,  10,     39",
			"OUTER_RING,    NIGHT,  11,     42",
	})
	void should_return_total_price_is_$4_given_taxi_locale_is_$1_and_boarding_at_$2_when_distance_is_$3(
			Locale locale, TimeRange timeRange, int distance, BigDecimal expectedTotalPrice) {
		TaxiTime boardingTime = getTaxiTime(timeRange == TimeRange.DAY ? 10 : 23, 0);
		Bill bill = new Taxi(locale).run(boardingTime, distance);
		assertEquals(expectedTotalPrice.setScale(2, BigDecimal.ROUND_FLOOR), bill.getTotalPrice().setScale(2, BigDecimal.ROUND_FLOOR));
		assertEquals(Integer.valueOf(distance), bill.getDistance());
		assertEquals(boardingTime.getDateTime(), bill.getBoardingTime());
	}


	@ParameterizedTest
	@ValueSource(ints = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22})
	void should_return_TimeRange_is_DAY_given_hour_is_(int hour) {
		assertEquals(TimeRange.DAY, getTaxiTime(hour, 0).getTimeRange());
	}

	@ParameterizedTest
	@ValueSource(ints = {23, 0, 1, 2, 3, 4, 5})
	void should_return_TimeRange_is_NIGHT_given_hour_is_(int hour) {
		assertEquals(TimeRange.NIGHT, getTaxiTime(hour, 0).getTimeRange());
	}

	@Test
	void should_throw_ex_given_trip_was_checkout_when_increase_distance() {
		BillingTrip trip = new Taxi(Locale.INNER_RING).start(getTaxiTime(10, 0));
		trip.checkout();
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> trip.increaseDistance(getTaxiTime(11, 0)));
		assertEquals("行程已结束", exception.getMessage());
	}

  	@Test
    void should_calculate_peak_period_price_if_at_peak() {
		Taxi taxi = new Taxi(Locale.INNER_RING);
		DefaultDayBillingStrategy defaultDayBillingStrategy = new DefaultDayBillingStrategy(taxi.getLocale());
		Range<LocalTime> peakPeriod = Range.closed(LocalTime.of(8, 0), LocalTime.of(9, 0));
		PeakBillingStrategy.PeakSettings peakSetting = new PeakBillingStrategy.PeakSettings(peakPeriod, BigDecimal.valueOf(1.5));
		PeakBillingStrategy peakBillingStrategy = new PeakBillingStrategy(defaultDayBillingStrategy, peakSetting);

		BillingTrip trip = taxi.start(getTaxiTime(7, 50), peakBillingStrategy);
		trip.increaseDistance(getTaxiTime(7,55));
		//peak period begin
		trip.increaseDistance(getTaxiTime(8,1));
		trip.increaseDistance(getTaxiTime(8,15));
		trip.increaseDistance(getTaxiTime(8,22));
		trip.increaseDistance(getTaxiTime(8,29));
		trip.increaseDistance(getTaxiTime(8,34));
		trip.increaseDistance(getTaxiTime(8,40));
		trip.increaseDistance(getTaxiTime(8,49));
		trip.increaseDistance(getTaxiTime(8,55));
		//peak period end
		trip.increaseDistance(getTaxiTime(9,2));

		Bill bill = trip.checkout();

		assertEquals(BigDecimal.valueOf(40.25),bill.getTotalPrice());
		assertEquals(Integer.valueOf(10),bill.getDistance());


	}

	private TaxiTime getTaxiTime(int hour, int minute) {
		return new TaxiTime(LocalDateTime.of(2018, 10, 1, hour, 0));
	}

}
