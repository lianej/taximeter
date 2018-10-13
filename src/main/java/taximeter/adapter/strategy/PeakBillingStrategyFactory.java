package taximeter.adapter.strategy;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import taximeter.domain.BillingStrategy;
import taximeter.domain.TaxiTime;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author yanweijin
 * @date 2018/10/13
 */
@Slf4j
public class PeakBillingStrategyFactory {

	private final ArrayList<PeakSettings> settings;

	public PeakBillingStrategyFactory(PeakSettings... settings) {
		this.settings = Lists.newArrayList(settings);
		checkPeakSettingsHasNotIntersection();
	}

	private void checkPeakSettingsHasNotIntersection() {
		for (int i = settings.size() - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				PeakPeriod peakPeriod1 = settings.get(i).getPeakPeriod();
				PeakPeriod peakPeriod2 = settings.get(j).getPeakPeriod();
				if (peakPeriod1.isIntersected(peakPeriod2)) {
					throw new IllegalArgumentException("高峰期配置不能有交集:" + peakPeriod1 + "," + peakPeriod2 + "");
				}
			}
		}
	}

	PeakBillingStrategy getPeakBillingStrategy(BillingStrategy billingStrategy) {
		return new PeakBillingStrategy(billingStrategy);
	}

	private class PeakBillingStrategy implements BillingStrategy {

		private BillingStrategy billingStrategy;
		private BiFunction<TaxiTime, BigDecimal, BigDecimal> peakPriceCalculator;

		public PeakBillingStrategy(BillingStrategy billingStrategy) {
			this.billingStrategy = billingStrategy;
			this.peakPriceCalculator = generatePriceRule(settings);
		}

		private BiFunction<TaxiTime, BigDecimal, BigDecimal> generatePriceRule(
				List<PeakSettings> settings) {
			return (taxiTime, currentPrice) ->
					settings
							.stream()
							.filter(setting -> setting.isPeakTime(taxiTime.getTime()))
							.findFirst()
							.map(PeakSettings::getRatio)
							.map(
									ratio -> {
										BigDecimal newPrice = currentPrice.multiply(ratio);
										log.trace("当前属于高峰期, 原价格:{}, 价格倍率:{}, 新价格:{}", currentPrice, ratio, newPrice);
										return newPrice;
									})
							.orElse(currentPrice);
		}

		@Override
		public BigDecimal getPriceOfWaitTime(LocalTime time) {
			return billingStrategy.getPriceOfWaitTime(time);
		}

		@Override
		public BigDecimal getStartingPrice(TaxiTime currentTime) {
			BigDecimal startingPrice = billingStrategy.getStartingPrice(currentTime);
			return peakPriceCalculator.apply(currentTime, startingPrice);
		}

		@Override
		public BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime) {
			BigDecimal currentPrice =
					billingStrategy.getCurrentDistanceUnitPrice(currentDistance, currentTime);
			return peakPriceCalculator.apply(currentTime, currentPrice);
		}
	}
}
