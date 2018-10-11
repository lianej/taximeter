package taximeter;

import java.math.BigDecimal;
import java.time.LocalTime;


public interface BillingStrategy {

	default BigDecimal getPriceOfWaitTime(LocalTime time) {
		return BigDecimal.ZERO;
	}

	BigDecimal getStartingPrice(TaxiTime currentTime);

	BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime);


}
