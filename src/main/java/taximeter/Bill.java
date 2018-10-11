package taximeter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author yanweijin
 * @date 2018/10/9
 */
public interface Bill {

	Integer getDistance();

	BigDecimal getTotalPrice();

	LocalDateTime getBoardingTime();
}
