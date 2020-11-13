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
 * Perform nominal and duration unittests on a large parking.
 */
final class LargeParkingTest extends ParkingTestBase {
	static private final Map<Duration, Float> parkingDurationToExpectedPrice = new HashMap<Duration, Float>() {
		private static final long serialVersionUID = 1L;
	{
		put(Duration.ofSeconds(0), 0.0f);
		put(Duration.ofMinutes(5), 0.0f);
		put(Duration.ofMinutes(15), 0.0f);
		put(Duration.ofHours(1), 0.7f);
		put(Duration.ofMinutes(90), 0.7f);
		put(Duration.ofHours(2), 1.4f);
		put(Duration.ofHours(3), 2.1f);
		put(Duration.ofHours(5), 3.5f);
		put(Duration.ofMinutes(570), 6.3f);
		put(Duration.ofDays(1), 16.8f);
		put(Duration.ofDays(2), 33.6f);
	}};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<Type, Integer> computeCarTypeToSlotsCount() {
		return new HashMap<Car.Type, Integer>() {
			private static final long serialVersionUID = 1L;
		{
			put(Car.Type.GASOLINE, 1000);
			put(Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY, 200);
			put(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY, 150);			
		}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PricingPolicy computePricingPolicy() {
		return new DefaultPricingPolicy(0.0f, 0.7f);
	}
		

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float getExpectedPrice(Car car, Duration parkingDuration) {
		return LargeParkingTest.parkingDurationToExpectedPrice.getOrDefault(parkingDuration, -1.0f);
	}
}