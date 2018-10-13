package taximeter.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public class Taxi {

	@Getter
	private Locale locale;

	private BillingStrategyFactory billingStrategyFactory;

	private Taximeter taximeter;

	public Taxi(Locale locale, BillingStrategyFactory billingStrategyFactory) {
		this.locale = locale;
		this.billingStrategyFactory = billingStrategyFactory;
		this.taximeter = new DefaultTaximeter();
	}

	public Bill run(TaxiTime boardingTime, Integer distance) {
		BillingTrip trip = taximeter.start(boardingTime);
		for (int i = 0; i < distance; i++) {
			trip.increaseDistance(boardingTime);
		}
		return trip.checkout();
	}

	public BillingTrip start(TaxiTime boardingTime) {
		return taximeter.start(boardingTime);
	}


	class DefaultTaximeter implements Taximeter {

		@Override
		public BillingTrip start(TaxiTime boardingTime) {
			return new BillingTripImpl(boardingTime);
		}

		private class BillingTripImpl implements BillingTrip {

			private boolean end;

			private Integer distance;

			private BigDecimal totalPrice;

			private TaxiTime boardingTime;

			private BigDecimal startingPrice;

			private BillingStrategy strategy;

			BillingTripImpl(TaxiTime boardingTime) {
				this.boardingTime = boardingTime;
				this.distance = 0;
				this.strategy = billingStrategyFactory.getBillingStrategy(Taxi.this, boardingTime);
				this.startingPrice = this.totalPrice = strategy.getStartingPrice(boardingTime);
			}

			@Override
			public void increaseDistance(TaxiTime currentTime) {
				checkTripIsStillNotEnd();
				this.distance += 1;
				BigDecimal currentDistancePrice = strategy.getCurrentDistanceUnitPrice(distance, currentTime);
				this.totalPrice = totalPrice.add(currentDistancePrice);
				log.trace("当前里程:{}, 本单位里程价格:{}", distance, currentDistancePrice);
			}

			@Override
			public Bill checkout() {
				this.end = true;
				log.trace("上车时间:{}, 起步价:{}, 里程:{}, 总价:{}", boardingTime, startingPrice, distance, totalPrice);
				return new BillImpl();
			}

			private void checkTripIsStillNotEnd() {
				if (end) {
					throw new IllegalStateException("行程已结束");
				}
			}

			@Getter
			@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
			private class BillImpl implements Bill {

				Integer distance;
				BigDecimal totalPrice;
				LocalDateTime boardingTime;

				BillImpl() {
					this.distance = BillingTripImpl.this.distance;
					this.totalPrice = BillingTripImpl.this.totalPrice;
					this.boardingTime = BillingTripImpl.this.boardingTime.getDateTime();
				}
			}

		}
	}
}
