/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import parking.api.Car;
import parking.api.DefaultCar;
import parking.api.DurationPricingPolicy;
import parking.api.Parking;
import parking.api.PricingPolicy;

/**
 * Perform unittests on a parking using a real time based clock.
 * @see parking.impl.DefaultClock
 */
final class RealTimeParkingTest {
	static private final float priceTolerance = 0.00001f;
	static private final Map<Integer, Float> parkingDurationToExpectedPrice = new HashMap<Integer, Float>() {
		private static final long serialVersionUID = 1L;
	{
		put(0, 1.0f);
		put(1, 1.1f);
		put(2, 1.2f);
	}};
	
	/**
	 * Custom pricing policy billing the car for per seconds.
	 * <p>Hourly basis of {@link parking.api.DefaultPricingPolicy} cannot be used as we cannot decently wait hours.</p>
	 */
	
	static private final class PerSecondPricingPolicy extends DurationPricingPolicy {
		@Override
		public float bill(Duration duration) {
			return 1.0f + (duration.toSeconds() * 0.1f); // This is a very expensive parking ;)
		}
	}
	
	private final Map<Car.Type, Integer> carTypeToSlotsCount;
	private final PricingPolicy pricingPolicy;
	private Parking parking;
	
	/**
	 * Instantiates a new real time parking test.
	 */
	RealTimeParkingTest() {
		this.carTypeToSlotsCount = new HashMap<Car.Type, Integer>() {
			private static final long serialVersionUID = 1L;
		{
			put(Car.Type.GASOLINE, 3);			
		}};
		this.pricingPolicy = new PerSecondPricingPolicy();
		this.parking = null;
	}
	
	/**
	 * Called just before each test method.
	 * <p>Create or recreate the parking to test.</p>
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.parking = Parking.create(this.carTypeToSlotsCount, this.pricingPolicy);
	}

	/**
	 * Called just after each test method.
	 * <p>Dispose the parking to test.</p>
	 */
	@AfterEach
	final void tearDown() throws Exception {
		this.parking = null;
	}

	/**
	 * Parameterized test parking a car and sleeping for a few seconds and verifying that the car is billed accordingly.
	 * @param parkingDurationInSeconds the parking duration in seconds
	 */
	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 2 })
	void parkForDuration(int parkingDurationInSeconds) {
		Car car = new DefaultCar("AQ-289-SP", Car.Type.GASOLINE);
		
		try {
			assertNotNull(this.parking.park(car));
			
			TimeUnit.SECONDS.sleep(parkingDurationInSeconds);
			
			assertEquals(parkingDurationToExpectedPrice.getOrDefault(parkingDurationInSeconds, -1.0f), this.parking.unparkAndBill(car), priceTolerance);
		} catch (Exception e) {
			fail(e);
		}
	}
}
