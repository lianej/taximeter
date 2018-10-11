package taximeter;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
class DefaultNightBillingStrategy implements BillingStrategy {
    private int paidDistance = 3;
    private Locale locale;

    DefaultNightBillingStrategy(Locale locale) {
        this.locale = locale;
    }

    @Override
    public BigDecimal getStartingPrice(TaxiTime currentTime) {
        return BigDecimal.valueOf(18);
    }

    @Override
    public BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime) {
        if (currentDistance > 10 && locale == Locale.INNER_RING) {
            return BigDecimal.valueOf(4.7);
        } else if (currentDistance > paidDistance) {
            return BigDecimal.valueOf(3);
        }
        return BigDecimal.ZERO;
    }
}
