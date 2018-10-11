package taximeter;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @author yanweijin
 * @date 2018/10/9
 */
public interface BillingStrategy {

	default BigDecimal getPriceOfWaitTime(LocalTime time) {
		return BigDecimal.ZERO;
	}

	BigDecimal getStartingPrice(TaxiTime currentTime);

	BigDecimal getCurrentDistanceUnitPrice(int currentDistance, TaxiTime currentTime);


}
