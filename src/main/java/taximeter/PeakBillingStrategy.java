package taximeter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.function.BiFunction;

@Slf4j
public class PeakBillingStrategy implements BillingStrategy {

  private BillingStrategy billingStrategy;
  private BiFunction<TaxiTime, BigDecimal, BigDecimal> peakPriceCalculator;

  public PeakBillingStrategy(BillingStrategy billingStrategy, PeakSettings... peakSettings) {
    this.billingStrategy = billingStrategy;
    ArrayList<PeakSettings> settings = Lists.newArrayList(peakSettings);
    this.peakPriceCalculator =
        (taxiTime, currentPrice) ->
            settings
                .stream()
                .filter(setting -> setting.isPeakTime(taxiTime.getTime()))
                .findFirst()
                .map(PeakSettings::getRatio)
                .map(ratio -> {
                      BigDecimal newPrice = currentPrice.multiply(ratio);
                      log.trace("当前属于高峰期, 原价格:{}, 价格倍率:{}, 新价格:{}", currentPrice, ratio, newPrice);
                      return newPrice;
                }).orElse(currentPrice);
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
      BigDecimal currentPrice = billingStrategy.getCurrentDistanceUnitPrice(currentDistance, currentTime);
      return peakPriceCalculator.apply(currentTime, currentPrice);
  }

}
