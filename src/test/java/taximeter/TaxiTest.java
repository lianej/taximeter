package taximeter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import taximeter.adapter.strategy.BaseBillingStrategyFactory;
import taximeter.adapter.strategy.PeakBillingStrategyFactory;
import taximeter.adapter.strategy.PeakSettings;
import taximeter.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalTime.of;
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
		Bill bill = new Taxi(locale, new BaseBillingStrategyFactory()).run(boardingTime, distance);
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
		BillingTrip trip = new Taxi(Locale.INNER_RING, new BaseBillingStrategyFactory()).start(getTaxiTime(10, 0));
		trip.checkout();
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> trip.increaseDistance(getTaxiTime(11, 0)));
		assertEquals("行程已结束", exception.getMessage());
	}

  	@Test
    void should_calculate_peak_period_price_if_at_peak() {
		PeakSettings peakSettingOfAM = newSettings(of(8, 0), of(9, 0), 1.5);
		PeakSettings peakSettingOfPM = newSettings(of(17, 0), of(18,30), 2);
	    BaseBillingStrategyFactory strategyFactory = new BaseBillingStrategyFactory();
	    PeakBillingStrategyFactory peakBillingStrategyFactory = new PeakBillingStrategyFactory(peakSettingOfAM, peakSettingOfPM);
	    strategyFactory.setPeakBillingStrategyFactory(peakBillingStrategyFactory);
	    Taxi taxi = new Taxi(Locale.INNER_RING, strategyFactory);


	    BillingTrip tripAtPeakOfAM = taxi.start(getTaxiTime(7, 50));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(7,55));
		//peak period begin
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,1));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,15));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,22));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,29));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,34));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,40));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,45));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,50));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,55));
		tripAtPeakOfAM.increaseDistance(getTaxiTime(8,59));
		//peak period end
		tripAtPeakOfAM.increaseDistance(getTaxiTime(9,2));

		Bill bill1 = tripAtPeakOfAM.checkout();
		assertEquals(BigDecimal.valueOf(49).setScale(2, BigDecimal.ROUND_HALF_UP), bill1.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
		assertEquals(Integer.valueOf(12),bill1.getDistance());

		//peak period begin
		BillingTrip tripAtPeakOfPM = taxi.start(getTaxiTime(17, 50));
		tripAtPeakOfPM.increaseDistance(getTaxiTime(17,55));
		tripAtPeakOfPM.increaseDistance(getTaxiTime(18,1));
		tripAtPeakOfPM.increaseDistance(getTaxiTime(18,15));
		tripAtPeakOfPM.increaseDistance(getTaxiTime(18,22));
		tripAtPeakOfPM.increaseDistance(getTaxiTime(18,29));
		//peak period end
		tripAtPeakOfPM.increaseDistance(getTaxiTime(18,34));
		tripAtPeakOfPM.increaseDistance(getTaxiTime(18,40));

		Bill bill2 = tripAtPeakOfPM.checkout();

		assertEquals(BigDecimal.valueOf(43).setScale(2, BigDecimal.ROUND_HALF_UP), bill2.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
		assertEquals(Integer.valueOf(7),bill2.getDistance());

	}

	@Test
	void should_throw_IAE_given_intersected_peak_periods() {
		PeakSettings peakSetting1 = newSettings(of(8, 0), of(9, 00), 1.5);
		PeakSettings peakSetting2 = newSettings(of(8, 30), of(9,30), 2);
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
				() -> new PeakBillingStrategyFactory(peakSetting1, peakSetting2));
		assertEquals("高峰期配置不能有交集:[08:30..09:30),[08:00..09:00)",iae.getMessage());
	}

	@Test
	void should_create_strategy_success_given_adjacent_peak_periods() {
		PeakSettings peakSetting1 = newSettings(of(8, 0), of(9, 0), 1.5);
		PeakSettings peakSetting2 = newSettings(of(9, 0), of(9, 30), 2);
		new PeakBillingStrategyFactory(peakSetting1, peakSetting2);
		new PeakBillingStrategyFactory(peakSetting2, peakSetting1);
	}

	private PeakSettings newSettings(LocalTime begin, LocalTime end, double ratio) {
		return new PeakSettings(begin, end, BigDecimal.valueOf(ratio));
	}

	private TaxiTime getTaxiTime(int hour, int minute) {
		return new TaxiTime(LocalDateTime.of(2018, 10, 1, hour, minute));
	}

}
