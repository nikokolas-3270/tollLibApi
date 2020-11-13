/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import parking.api.Car;
import parking.api.Car.Type;
import parking.api.DefaultCar;
import parking.api.DefaultPricingPolicy;
import parking.api.ParkingException;
import parking.api.PricingPolicy;

/**
 * Perform nominal and duration unittests on a parking where only electrical cars with a 20kW power supply are allowed.
 * <p>There are also some additional tests to check that gasoline and electrical cars with a 50kW power supply cannot be parked.</p>
 */
final class ElectricWith20kWPowerSupplyOnlyParkingTest extends ParkingTestBase {
	static private final Map<Duration, Float> parkingDurationToExpectedPrice = new HashMap<Duration, Float>() {
		private static final long serialVersionUID = 1L;
	{
		put(Duration.ofSeconds(0), 0.0f);
		put(Duration.ofMinutes(5), 0.0f);
		put(Duration.ofMinutes(15), 0.0f);
		put(Duration.ofHours(1), 2.5f);
		put(Duration.ofMinutes(90), 2.5f);
		put(Duration.ofHours(2), 5.0f);
		put(Duration.ofHours(3), 7.5f);
		put(Duration.ofHours(5), 12.5f);
		put(Duration.ofMinutes(570), 22.5f);
		put(Duration.ofDays(1), 60.0f);
		put(Duration.ofDays(2), 120.0f);
	}};
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<Type, Integer> computeCarTypeToSlotsCount() {
		return new HashMap<Car.Type, Integer>() {
			private static final long serialVersionUID = 1L;
		{
			put(Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY, 3);
			put(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY, 0);			
		}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PricingPolicy computePricingPolicy() {
		return new DefaultPricingPolicy(0.0f, 2.5f);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected Collection<Car> computeNominalTestCars() {
		return new Vector<Car>() {
			private static final long serialVersionUID = 1L;
		{
			add(new DefaultCar("NG-289-SP", Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
			add(new DefaultCar("CZ-327-TY", Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
			add(new DefaultCar(null, Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
		}};
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float getExpectedPrice(Car car, Duration parkingDuration) {
		return ElectricWith20kWPowerSupplyOnlyParkingTest.parkingDurationToExpectedPrice.getOrDefault(parkingDuration, -1.0f);
	}
	
	/**
	 * The ArgumentsProvider for 3 cars of a given car type.
	 * <p>Car type is passed to the constructor.</p>
	 */
	static private abstract class CarArgumentProviderForType extends AbstractCarArgumentProvider {
		private final Car.Type carType;
		
		/**
		 * Instantiates a new car argument provider for type.
		 * @param carType the car type
		 */
		protected CarArgumentProviderForType(Car.Type carType) {
			this.carType = carType;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Collection<Car> getCars(ParkingTestBase testInstance) {
			return new Vector<Car>() {
				private static final long serialVersionUID = 1L;
			{
				add(new DefaultCar("NG-289-SP", carType));
				add(new DefaultCar("CZ-327-TY", carType));
				add(new DefaultCar(null, carType));
			}};
		}
	}
	
	/**
	 * The ArgumentsProvider for 3 gasoline cars.
	 */
	static final class GasolineCarArgumentProvider extends CarArgumentProviderForType {
		/**
		 * Instantiates a new gasoline car argument provider.
		 */
		GasolineCarArgumentProvider() {
			super(Car.Type.GASOLINE);
		}
	}
	
	/**
	 * Parameterized test checking a gasoline car cannot be parked in this parking.
	 * <p>A {@link ParkingException} is thrown as the car type is not even known by the parking.</p>
	 * @see parking.api.Parking#create(Map, PricingPolicy, parking.api.Clock)
	 * @see parking.api.Parking#park(Car)
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(GasolineCarArgumentProvider.class)
	void cannotParkGasolineCarTest(Car car) {
	 	assertFalse(this.parking.isParked(car));
		try {
			this.parking.park(car);
			fail("Should throw a ParkingException");
		} catch (ParkingException e) {
		}
	 	assertFalse(this.parking.isParked(car));
	}
	
	/**
	 * The ArgumentsProvider for 3 electrical cars with a 50 kW power supply.
	 */
	static final class ElectricWith50kWPowerSupplyCarArgumentProvider extends CarArgumentProviderForType {
		/**
		 * Instantiates a new electric with 50 kW power supply car argument provider.
		 */
		ElectricWith50kWPowerSupplyCarArgumentProvider() {
			super(Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY);
		}
	}
	
	/**
	 * Parameterized test checking an electrical car with 50 kW power supply cannot be parked in this parking.
	 * <p>A {@link parking.api.Parking#park(Car)} returns null as there is no capacity for this car type.</p>
	 * @see parking.api.Parking#create(Map, PricingPolicy, parking.api.Clock)
	 * @see parking.api.Parking#park(Car)
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(ElectricWith50kWPowerSupplyCarArgumentProvider.class)
	void cannotParkElectricWith50kWPowerSupplyCarTest(Car car) {
	 	assertFalse(this.parking.isParked(car));
		try {
			assertNull(this.parking.park(car));
		} catch (ParkingException e) {
			fail(e);
		}
	 	assertFalse(this.parking.isParked(car));
	}
}
