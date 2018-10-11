package taximeter;

import lombok.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yanweijin
 * @date 2018/10/9
 */
@Value
public class TaxiTime {

	private LocalDateTime dateTime;

	TaxiTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	TimeRange getTimeRange() {
		switch (dateTime.toLocalTime().getHour()) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 23:
				return TimeRange.NIGHT;
			default:
				return TimeRange.DAY;
		}
	}

	@Override
	public String toString() {
		return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
	}
}
