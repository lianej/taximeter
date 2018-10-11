package taximeter;

import java.math.BigDecimal;

class DefaultDayBillingStrategy implements BillingStrategy {
    private Locale locale;

    DefaultDayBillingStrategy(Locale locale) {
        this.locale = locale;
    }

    @Override
    public BigDecimal getStartingPrice(TaxiTime currentTime) {
        return BigDecimal.valueOf(14);
    }

    @Override
    public BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime) {
        int paidDistance = 3;
        if (currentDistance > 10 && locale == Locale.INNER_RING) {
            return BigDecimal.valueOf(3.5);
        } else if (currentDistance > paidDistance) {
            return BigDecimal.valueOf(2.5);
        }
        return BigDecimal.ZERO;
    }
}
