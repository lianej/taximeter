package taximeter.domain;

/**
 * @author yanweijin
 * @date 2018/10/10
 */
public interface Taximeter {
	BillingTrip start(TaxiTime boardingTime);
}
