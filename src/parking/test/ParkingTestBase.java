/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import parking.api.Car;
import parking.api.DefaultCar;
import parking.api.Parking;
import parking.api.ParkingException;
import parking.api.PricingPolicy;

/**
 * The base abstract class of many unittest.
 */
/* The ParkingTestBase instance need to be re-created for each test
 * to be able to retrieve the test instance from an ExtensionContext */
@TestInstance(Lifecycle.PER_CLASS)
abstract class ParkingTestBase {
	static private final float priceTolerance = 0.00001f;
	
	/**
	 * The ArgumentsProvider for cars.
	 * <p>Designed to feed parameterized tests with cars.</p>
	 */
	static protected abstract class AbstractCarArgumentProvider implements ArgumentsProvider {
		/**
		 * Get the cars.
		 * @param testInstance the test instance
		 * @return the cars
		 */
		protected abstract Collection<Car> getCars(ParkingTestBase testInstance); 

        /**
         * Return the argument stream wrapping the cars returned by {@link #getCars(ParkingTestBase)} arguments.
         * @param context the context
         * @return the stream
         */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        	Collection<Arguments> arguments = new Vector<Arguments>();
        	
        	for (Car car: getCars((ParkingTestBase) context.getTestInstance().get())) {
        		arguments.add(new Arguments() {
					@Override
					public Object[] get() {
						return new Object[] {car};
					}
        		});
        	}
        	
            return arguments.stream();
        }
    }

	private final Map<Car.Type, Integer> carTypeToSlotsCount;
	private final PricingPolicy pricingPolicy; 
	protected final ClockMock clock;	

	protected Parking parking;
	private final Collection<Car> nominalTestsCars;
	
	/**
	 * Instantiates a new parking test base.
	 */
	protected ParkingTestBase() {
		this.carTypeToSlotsCount = computeCarTypeToSlotsCount();
		this.pricingPolicy = computePricingPolicy();
		this.clock = new ClockMock();
		this.parking = null;
		this.nominalTestsCars = computeNominalTestCars();
	}
	
	/**
	 * Compute the carTypeToSlotsCount argument to pass to {@link Parking#create(Map, PricingPolicy, parking.api.Clock)}.
	 * <p>Called by the test constructor.</p>
	 * @return the map associating each car type to its number of slots/capacity
	 */
	protected abstract Map<Car.Type, Integer> computeCarTypeToSlotsCount();
	
	/**
	 * Return the number of slots/parking capacity associated to a given car type.
	 * @param carType the car type, not null
	 * @return the slots count/parking capacity
	 */
	protected final int getSlotsCountForCarType(Car.Type carType) {
		return this.carTypeToSlotsCount.getOrDefault(carType, -1);
	}
	
	/**
	 * Compute the pricingPolicy argument to pass to {@link Parking#create(Map, PricingPolicy, parking.api.Clock)}.
	 * <p>Called by the test constructor.</p>
	 * @return the pricing policy
	 */
	protected abstract PricingPolicy computePricingPolicy();

	/**
	 * Called just before each test method.
	 * <p>Create or recreate the parking to test.</p>
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.parking = Parking.create(this.carTypeToSlotsCount, this.pricingPolicy, this.clock);
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
	 * Compute the list of cars to test on nominal tests.
	 * <p>Those nominal tests are the one using the {@link CarArgumentProvider} argument provider</p>
	 * @return a non null list of non null cars
	 */
	protected Collection<Car> computeNominalTestCars() {
		return new Vector<Car>() {
			private static final long serialVersionUID = 1L;
		{
			add(new DefaultCar("AI-241-SP", Car.Type.GASOLINE));
			add(new DefaultCar("AI-241-SP", Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
			add(new DefaultCar("AI-241-SP", Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY));
			add(new DefaultCar("8545 TY 68", Car.Type.GASOLINE));
			add(new DefaultCar("8545 TY 68", Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
			add(new DefaultCar("8545 TY 68", Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY));
			add(new DefaultCar(null, Car.Type.GASOLINE));
			add(new DefaultCar(null, Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
			add(new DefaultCar(null, Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY));
		}};
	}
	
	/**
	 * The argument provider for cars returned by {@link ParkingTestBase#computeNominalTestCars()}.
	 * <p>Designed to feed nominal parameterized tests with cars.</p>
	 */
	static protected final class CarArgumentProvider extends AbstractCarArgumentProvider {
		/**
		 * Returns the cars returned by {@link ParkingTestBase#computeNominalTestCars()}.
		 * @param testInstance the test instance
		 * @return the cars
		 */
		@Override
		protected Collection<Car> getCars(ParkingTestBase testInstance) {
			return testInstance.nominalTestsCars;
		}
	}
	
	/**
	 * Gets the expected price.
	 * @param car the car
	 * @param parkingDuration the parking duration
	 * @return the expected price
	 */
	protected abstract float getExpectedPrice(Car car, Duration parkingDuration);
	
	/**
	 * Nominal parameterized test checking that a car can be parked and then unparked on a brand new parking where no car is parked.
	 * <p>Car is parked for 2 hours. Parking bill is checked accordingly.</p>
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)
	void nominalTest(Car car) {
		Duration parkingDuration = Duration.ofHours(2);
		
		assertFalse(this.parking.isParked(car));
		try {
			assertNotNull(this.parking.park(car));
			assertTrue(this.parking.isParked(car));
			
			this.clock.increment(parkingDuration);
			
			assertTrue(this.parking.isParked(car));
			assertEquals(getExpectedPrice(car, parkingDuration), this.parking.unparkAndBill(car), priceTolerance);
		} catch (ParkingException e) {
			fail(e);
		}
		assertFalse(this.parking.isParked(car));
	}
	
	/**
	 * Nominal parameterized test checking that a car cannot be parked if it is already parked.
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)
	void cannotParkIfAlreadyParkedTest(Car car) {
		assertFalse(this.parking.isParked(car));
		try {
			assertNotNull(this.parking.park(car));
			assertTrue(this.parking.isParked(car));
		} catch (ParkingException e) {
			fail(e);
		}
		
		try {
			assertTrue(this.parking.isParked(car));
			this.parking.park(car);
			fail("Should throw a ParkingException");
		} catch (ParkingException e) {
		}
		assertTrue(this.parking.isParked(car));
	}

	/**
	 * Utility method filling the parking for a given car type.
	 * <p>The number of cars this method is parking is: lastRegistrationIndex - firstRegistrationIndex.</p>
	 * <p>No car is parked if lastRegistrationIndex &le; firstRegistrationIndex.</p>
	 * <p>Method also checks that all parked cars get a different parking slot.</p>
	 * @param carType the car type
	 * @param firstRegistrationIndex the first registration index
	 * @param lastRegistrationIndex the last registration index
	 * @return the map associating registration number generated by this method to parking slot id filled by this method
	 */
	protected final Map<String, String> fillParkingAndReturnsRegistrationNumberToSlotId(Car.Type carType, int firstRegistrationIndex, int lastRegistrationIndex) {
		Map<String, String> registrationNumberToSlotId = new HashMap<String, String>();

		try {
			for (int registrationIndex = firstRegistrationIndex; registrationIndex < lastRegistrationIndex; registrationIndex++) {
				String registrationNumber = Integer.toString(registrationIndex);
				Car car = new DefaultCar(registrationNumber, carType);
				
				assertFalse(this.parking.isParked(car));
				
				String slotId = this.parking.park(car);
				
				assertNotNull(slotId);
				assertTrue(this.parking.isParked(car));
				assertFalse(registrationNumberToSlotId.containsValue(slotId)); // Maybe not efficient but who care in a unit-test
				registrationNumberToSlotId.put(registrationNumber, slotId);
			}
		} catch (ParkingException e) {
			fail(e);
		}
		
		return registrationNumberToSlotId;
	}
	
	/**
	 * Nominal parameterized test checking that a car cannot be parked if the parking capacity is reached.
	 * <p>Test also checks that all parked cars get a different parking slot.</p>
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)
	void cannotParkIfCapacityReachedTest(Car car) {
		Car.Type carType = car.getType();
		fillParkingAndReturnsRegistrationNumberToSlotId(carType, 0, getSlotsCountForCarType(carType));
		
		assertFalse(this.parking.isParked(car));
		try {
			assertNull(this.parking.park(car));
		} catch (ParkingException e) {
			fail(e);
		}
		assertFalse(this.parking.isParked(car));
	}
	
	/**
	 * Nominal parameterized test checking that a car can be parked if all parking slot except one are taken.
	 * <p>Test also checks that all parked cars get a different parking slot.</p>
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)
	void canParkIfCapacityNearlyReachedTest(Car car) {
		Car.Type carType = car.getType();
		Map<String, String> registrationNumberToSlotId = fillParkingAndReturnsRegistrationNumberToSlotId(carType, 0, getSlotsCountForCarType(carType)-1);
		
		assertFalse(this.parking.isParked(car));
		try {
			String slotId = this.parking.park(car);
			
			assertNotNull(slotId);
			assertFalse(registrationNumberToSlotId.containsValue(slotId)); // Maybe not efficient but who care in a unit-test
		} catch (ParkingException e) {
			fail(e);
		}
		assertTrue(this.parking.isParked(car));
	}
	
	/**
	 * Nominal parameterized test checking that a car can be parked if the parking is full and a slot just get released.
	 * <p>Test also checks that all parked cars get a different parking slot.</p>
	 * <p>Test check that the slot which is associated is the one that just got released</p>
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)
	void canParkIfCapacityReachedButASlotIsReleasedTest(Car car) {
		Duration parkingDuration = Duration.ofHours(1);
		// We also check that all returned slot IDs are all different
		
		Car.Type carType = car.getType();
		int slotsCount = getSlotsCountForCarType(carType);
		Map<String, String> registrationNumberToSlotId = fillParkingAndReturnsRegistrationNumberToSlotId(carType, 0, slotsCount);
		String departingCarRegistrationNumber = Integer.toString(new Random().nextInt(slotsCount));
		Car departingCar = new DefaultCar(departingCarRegistrationNumber, carType);
		
		assertTrue(registrationNumberToSlotId.containsKey(departingCarRegistrationNumber));
		this.clock.increment(parkingDuration);
		
		assertTrue(this.parking.isParked(departingCar));
		try {
			assertEquals(getExpectedPrice(departingCar, parkingDuration), this.parking.unparkAndBill(departingCar), priceTolerance);
			assertFalse(this.parking.isParked(departingCar));
			assertFalse(this.parking.isParked(car));
			assertEquals(registrationNumberToSlotId.get(departingCarRegistrationNumber), this.parking.park(car));
		} catch (ParkingException e) {
			fail(e);
		}
		assertTrue(this.parking.isParked(car));
	}

	/**
	 * Nominal parameterized test checking that a car cannot be unparked if it was not parked first.
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)
	void cannotUnparkIfNotParkedTest(Car car) {
		assertFalse(this.parking.isParked(car));
		try {
			this.parking.unparkAndBill(car);
			fail("Should throw a ParkingException");
		} catch (ParkingException e) {
		}
		assertFalse(this.parking.isParked(car));
	}
	
	/**
	 * Nominal parameterized test checking that a car can be unparked even if its type changed while it was parked.
	 * <p>We also checked that the car is billed according the new type.</p>
	 * @param car the car
	 */
	@ParameterizedTest
	@ArgumentsSource(CarArgumentProvider.class)	
	void canUnparkEvenIfTypeChangeTest(Car car) {
		Duration parkingDuration = Duration.ofMinutes(90);
		
		assertFalse(this.parking.isParked(car));
		try {
			assertNotNull(this.parking.park(car));
			assertTrue(this.parking.isParked(car));
			
			this.clock.increment(parkingDuration);
			
			car = new DefaultCar(car.getRegistrationNumber(), (car.getType() == Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY ? Car.Type.ELECTRIC_WITH_50kW_POWER_SUPPLY : Car.Type.ELECTRIC_WITH_20kW_POWER_SUPPLY));
			
			assertEquals(getExpectedPrice(car, parkingDuration), this.parking.unparkAndBill(car), priceTolerance);
		} catch (ParkingException e) {
			fail(e);
		}
		assertFalse(this.parking.isParked(car));
	}
	
	/** The default durations to inspect. */
	static protected final Duration[] defaultParkingDurations = new Duration[] {
		Duration.ofSeconds(0),
		Duration.ofMinutes(5),
		Duration.ofMinutes(15),
		Duration.ofHours(1),
		Duration.ofMinutes(90),
		Duration.ofHours(2),
		Duration.ofHours(3),
		Duration.ofHours(5),
		Duration.ofMinutes(570),
		Duration.ofDays(1),
		Duration.ofDays(2)
	};
	
	/**
	 * The argument provider for car and duration pairs.
	 * <p>Designed to feed duration parameterized tests with those pairs.</p>
	 * <p>The number of pairs provided is: number of cars x number of durations. In other words all possible pairs are generated from the list of cars and the list of durations.</p>
	 * <p>Cars are filtered from list returned by {@link ParkingTestBase#computeNominalTestCars()} by removing cars with a null registration number and only keeping one car per car type.</p>
	 * <p>Durations are passed to the constructor.</p>
	 */
	static protected abstract class AbstractCarAndDurationArgumentsProvider implements ArgumentsProvider {
		private final Duration[] parkingDurations;
		
		/**
		 * Instantiates a new abstract car and duration arguments provider.
		 * @param parkingDurations the non null array of non null parking durations
		 */
		protected AbstractCarAndDurationArgumentsProvider(Duration[] parkingDurations) {
			this.parkingDurations = parkingDurations;
		}
		
		/**
	     * Return the argument stream wrapping the pairs.
	     * @param context the context
	     * @return the stream
	     */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        	Collection<Arguments> arguments = new Vector<Arguments>();
        	Set<Car.Type> knownCarTypes = new HashSet<Car.Type>();
        	
        	for (Car car: ((ParkingTestBase) context.getTestInstance().get()).nominalTestsCars) {
        		if (car.getRegistrationNumber() != null) {
        			Car.Type carType = car.getType();
        			
        			if (!knownCarTypes.contains(carType)) {
        				knownCarTypes.add(carType);
		        		for (Duration parkingDuration: this.parkingDurations) {
			        		arguments.add(new Arguments() {
								@Override
								public Object[] get() {
									return new Object[] {car, parkingDuration};
								}
			        		});
		        		}
        			}
        		}
        	}
        	
            return arguments.stream();
        }
    }
	
	/**
	 * The argument provider for car and duration pairs with durations coming from {@link ParkingTestBase#defaultParkingDurations}
	 */
	static protected final class CarAndDurationArgumentsProvider extends AbstractCarAndDurationArgumentsProvider {
		/**
		 * Instantiates a new car and duration arguments provider.
		 */
		protected CarAndDurationArgumentsProvider() {
			super(defaultParkingDurations);
		}
	}
	
	/**
	 * Duration parameterized test checking the billing price against the expected price
	 * @param car the car
	 * @param parkingDuration the parking duration
	 */
	@ParameterizedTest
	@ArgumentsSource(CarAndDurationArgumentsProvider.class)
	void billForDurationTest(Car car, Duration parkingDuration) {
		assertFalse(this.parking.isParked(car));
		try {
			assertNotNull(this.parking.park(car));
			assertTrue(this.parking.isParked(car));
			
			this.clock.increment(parkingDuration);
			
			assertTrue(this.parking.isParked(car));
			assertEquals(getExpectedPrice(car, parkingDuration), this.parking.unparkAndBill(car), priceTolerance);
		} catch (ParkingException e) {
			fail(e);
		}
		assertFalse(this.parking.isParked(car));
	}
}
