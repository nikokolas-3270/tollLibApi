/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import parking.api.Car;
import parking.api.Car.Type;
import parking.api.DefaultPricingPolicy;
import parking.api.PricingPolicy;

/**
 * Perform nominal and duration unittests on a small parking.
 */
final class SmallParkingTest extends ParkingTestBase {
	static private final Map<Duration, Float> parkingDurationToExpectedPrice = new HashMap<Duration, Float>() {
		private static final long serialVersionUID = 1L;
	{
		put(Duration.ofSeconds(0), 0.0f);
		put(Duration.ofMinutes(5), 0.0f);
		put(Duration.ofMinutes(15), 0.0f);
		put(Duration.ofHours(1), 1.5f);
		put(Duration.ofMinutes(90), 1.5f);
		put(Duration.ofHours(2), 3.0f);
		put(Duration.ofHours(3), 4.5f);
		put(Duration.ofHours(5), 7.5f);
		put(Duration.ofMinutes(570), 13.5f);
		put(Duration.ofDays(1), 36.0f);
		put(Duration.ofDays(2), 72.0f);
	}};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<Type, Integer> computeCarTypeToSlotsCount() {
		return new HashMap<Car.Type, Integer>() {
			private static final long serialVersionUID = 1L;
		{
			put(Car.Type.GASOLINE, 5);
			put(Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY, 2);
			put(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY, 2);			
		}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PricingPolicy computePricingPolicy() {
		return new DefaultPricingPolicy(0.0f, 1.5f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float getExpectedPrice(Car car, Duration parkingDuration) {
		return SmallParkingTest.parkingDurationToExpectedPrice.getOrDefault(parkingDuration, -1.0f);
	}
}
