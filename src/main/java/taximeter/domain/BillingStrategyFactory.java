package taximeter.domain;

/**
 * @author yanweijin
 * @date 2018/10/13
 */
public interface BillingStrategyFactory {
	BillingStrategy getBillingStrategy(Taxi taxi, TaxiTime currentTime);
}
