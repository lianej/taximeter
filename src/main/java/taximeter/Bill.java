package taximeter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface Bill {

	Integer getDistance();

	BigDecimal getTotalPrice();

	LocalDateTime getBoardingTime();
}
