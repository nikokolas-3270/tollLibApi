/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import parking.api.Car;
import parking.api.DefaultPricingPolicy;
import parking.api.Car.Type;
import parking.api.PricingPolicy;

/**
 * Perform nominal and duration unittests on a parking for which there is fixed amount to bill.
 * <p>In other words, the first hour is not free with the default pricing policy on this parking.</p>
 * @see DefaultParkingPolicy
 */
final class FixedAmountPricingParkingTest extends ParkingTestBase {
	static private final Map<Duration, Float> parkingDurationToExpectedPrice = new HashMap<Duration, Float>() {
		private static final long serialVersionUID = 1L;
	{
		put(Duration.ofSeconds(0), 4.3f);
		put(Duration.ofMinutes(5), 4.3f);
		put(Duration.ofMinutes(15), 4.3f);
		put(Duration.ofHours(1), 5.8f);
		put(Duration.ofMinutes(90), 5.8f);
		put(Duration.ofHours(2), 7.3f);
		put(Duration.ofHours(3), 8.8f);
		put(Duration.ofHours(5), 11.8f);
		put(Duration.ofMinutes(570), 17.8f);
		put(Duration.ofDays(1), 40.3f);
		put(Duration.ofDays(2), 76.3f);
	}};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<Type, Integer> computeCarTypeToSlotsCount() {
		return new HashMap<Car.Type, Integer>() {
			private static final long serialVersionUID = 1L;
		{
			put(Car.Type.GASOLINE, 25);
			put(Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY, 7);
			put(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY, 7);			
		}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PricingPolicy computePricingPolicy() {
		return new DefaultPricingPolicy(4.3f, 1.5f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float getExpectedPrice(Car car, Duration parkingDuration) {
		return FixedAmountPricingParkingTest.parkingDurationToExpectedPrice.getOrDefault(parkingDuration, -1.0f);
	}
}
