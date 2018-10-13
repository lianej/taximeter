package taximeter.domain;

/**
 * @author yanweijin
 * @date 2018/10/10
 */
public interface BillingTrip {

	void increaseDistance(TaxiTime currentTime);

	Bill checkout();
}
