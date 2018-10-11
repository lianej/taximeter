package taximeter;

import lombok.Value;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


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

	LocalTime getTime(){
		return this.dateTime.toLocalTime();
	}

	@Override
	public String toString() {
		return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
	}
}
