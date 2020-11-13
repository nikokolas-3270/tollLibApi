/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import parking.api.Car;
import parking.api.Car.Type;
import parking.api.PricingPolicy;

/**
 * Perform nominal and duration unittests on a parking with a custom pricing policy.
 * @see CustomPricingPolicy#bill(Car, Instant, Instant)
 */
final class CustomPricingParkingTest extends ParkingTestBase {
	static private final Map<Car.Type, Map<Duration, Float>> carTypeToparkingDurationToExpectedPrice = new HashMap<Car.Type, Map<Duration, Float>>() {
		private static final long serialVersionUID = 1L;
	{
		put(Car.Type.GASOLINE, new HashMap<Duration, Float>() {
			private static final long serialVersionUID = 1L;
			{
				put(Duration.ofSeconds(0), 0.0f);
				put(Duration.ofMinutes(5), 0.0f);
				put(Duration.ofMinutes(15), 0.0f);
				put(Duration.ofHours(1), 5.0f);
				put(Duration.ofMinutes(90), 5.0f);
				put(Duration.ofHours(2), 5.0f);
				put(Duration.ofHours(3), 5.0f);
				put(Duration.ofHours(5), 5.0f);
				put(Duration.ofMinutes(570), 5.0f);
				put(Duration.ofDays(1), 5.0f);
				put(Duration.ofDays(2), 5.0f);
			}});
		put(Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY, new HashMap<Duration, Float>() {
			private static final long serialVersionUID = 1L;
			{
				put(Duration.ofSeconds(0), 0.0f);
				put(Duration.ofMinutes(5), 0.15f);
				put(Duration.ofMinutes(15), 0.45f);
				put(Duration.ofHours(1), 6.8f);
				put(Duration.ofMinutes(90), 7.7f);
				put(Duration.ofHours(2), 8.6f);
				put(Duration.ofHours(3), 10.4f);
				put(Duration.ofHours(5), 14.0f);
				put(Duration.ofMinutes(570), 22.1f);
				put(Duration.ofDays(1), 48.2f);
				put(Duration.ofDays(2), 91.4f);
			}});
		put(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY, new HashMap<Duration, Float>() {
			private static final long serialVersionUID = 1L;
			{
				put(Duration.ofSeconds(0), 0.0f);
				put(Duration.ofMinutes(5), 0.3f);
				put(Duration.ofMinutes(15), 0.9f);
				put(Duration.ofHours(1), 8.6f);
				put(Duration.ofMinutes(90), 10.4f);
				put(Duration.ofHours(2), 12.2f);
				put(Duration.ofHours(3), 15.8f);
				put(Duration.ofHours(5), 23.0f);
				put(Duration.ofMinutes(570), 39.2f);
				put(Duration.ofDays(1), 91.4f);
				put(Duration.ofDays(2), 177.8f);
			}});
	}};
	
	/**
	 * The pricing policy billing gasoline and electrical cars differently.
	 */
	static final class CustomPricingPolicy implements PricingPolicy {
		/**
		 * Bill a car according its arrival and departure instant.
		 * <p>Electrical cars are billed for the electricity.</p>
		 * <p>All cars are billed with a fixed amount, there is no hourly amount to pay except for the electricity.</p>
		 * @param car the car to bill
		 * @param arrivalInstant the arrival instant, not null, before the departure instant
		 * @param departureInstant the departure instant, not null, after the arrival instant
		 * @return the price to pay to leave the parking
		 */
		@Override
		public float bill(Car car, Instant arrivalInstant, Instant departureInstant) {
			float occupationAmount;
			
			if (arrivalInstant.plus(Duration.ofMinutes(30)).isAfter(departureInstant)) {
				occupationAmount = 0.0f; // The first 30 minutes are free
			} else {
				occupationAmount = 5.0f; // Then you get charged a fixed amount whatever time you stay
			}			
			
			float electricalAmountPerSecond;
			
			switch (car.getType()) {
			case ELECTRIC_WITH_20kW_POWER_SUPPLY:
				electricalAmountPerSecond = 0.0005f;
				break;
			case ELECTRIC_WITH_50kW_POWER_SUPPLY:
				electricalAmountPerSecond = 0.001f;
				break;
			default: // GASOLINE
				electricalAmountPerSecond = 0.0f;
			}

			return occupationAmount + Duration.between(arrivalInstant, departureInstant).toSeconds() * electricalAmountPerSecond;
		}
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<Type, Integer> computeCarTypeToSlotsCount() {
		return new HashMap<Car.Type, Integer>() {
			private static final long serialVersionUID = 1L;
		{
			put(Car.Type.GASOLINE, 42);
			put(Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY, 23);
			put(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY, 11);			
		}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PricingPolicy computePricingPolicy() {
		return new CustomPricingPolicy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float getExpectedPrice(Car car, Duration parkingDuration) {
		return carTypeToparkingDurationToExpectedPrice.get(car.getType()).getOrDefault(parkingDuration, -1.0f);
	}
}
