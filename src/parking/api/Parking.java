/*
 * Part of the 'Parking toll library'
 */
package parking.api;

import java.util.Map;

/**
 * The Interface Parking.
 * <p>This is the core of the 'Parking toll library' API.</p>
 * <p><b>Implementing this interface is discouraged. This interface only exist in order to decouple the parking API from the implementation.</b></p>
 * <p>All parking operations must be done through this interface. Creating a parking must be done through one of the static create methods.</p>
 * </p>
 */
public interface Parking {
	/**
	 * Create a new Parking object.
	 * <p>Number of slot/parking capacity for each car type must be specified with the carTypeToSlotsCount parameter:<ul>
	 * <li>Creation will not fail if invalid (negative) values are provided, the capacity will just be considered to be zero in that case.
	 * <li>It is not mandatory to give a value for all possible car types but parking (i.e calling {@link #park(Car)}) a car for which the parking capacity
	 * has not been specified will throw a {@link ParkingException}
	 * </ul>
	 * <p>The given pricing policy and the given clock are used to bill the car leaving the parking in {@link #unparkAndBill(Car)} method.</p>
	 * <p>Given clock be null in which case a real time default clock using is provided.</p>
	 * @param carTypeToSlotsCount the number of slots for each car type, not null
	 * @param pricingPolicy the pricing policy, not null
	 * @param clock the clock, can be null
	 * @return the parking, not null
	 */
	static Parking create(Map<Car.Type, Integer> carTypeToSlotsCount, PricingPolicy pricingPolicy, Clock clock) {
		return ParkingFactory.Singleton.getInstance().create(carTypeToSlotsCount, pricingPolicy, clock);
	}
	
	/**
	 * Create a new Parking object.
	 * <p>Just call the other create method with a null clock.</p>
	 * @see Parking#create(Map, PricingPolicy, Clock)
	 * @param carTypeToSlotsCount the number of slots for each car type, not null
	 * @param pricingPolicy the pricing policy, not null
	 * @return the parking, not null
	 */
	static Parking create(Map<Car.Type, Integer> carTypeToSlotsCount, PricingPolicy pricingPolicy) {
		return Parking.create(carTypeToSlotsCount, pricingPolicy, null);
	}
	
	/**
	 * Park a car.
	 * <p>Allocates a parking slot according the car type. Returns the slot id if the allocation is successful,
	 * this returned id is different than the ids previously returned by the method and associated to other cars.
	 * Null is returned if there is no remaining parking slot/capacity for the car type; in that case the car is not parked.</p>
	 * <p>Method may also throws a {@link ParkingException} exception if the given car is already parked of if the car type is unknown as
	 * no slots count/capacity was specified for it at parking creation; if that happens the car won't be parked.</p>
	 * <p>Method may also involuntary throw or propagate {@link RuntimeException} exceptions maybe thrown by {@link Car#getRegistrationNumber()}, {@link Car#getType()} or
	 * {@link Clock#instant()}; if that happens, this won't corrupt the internal data of the parking and the car will either be parked or not parked at the end of the method.</p>
	 * <p>Default implementation is thread safe.</p>
	 * @see Parking#unparkAndBill(Car)
	 * @param car the car, not null
	 * @return the parking slot, not null if the car was parked, null if the car was not parked
	 * @throws ParkingException if the car is already parked or if no capacity was specified for the car type at parking creation 
	 */
	String park(Car car) throws ParkingException;
	
	/**
	 * Unpark a car and bill it.
	 * <p>Release the slot id previously allocated to the car by {@link Parking#park(Car)} and returns the price the driver of the car has to pay.</p>
	 * <p>Throws a {@link ParkingException} exception if the given car was not parked that is to say if {@link Parking#park(Car)} was never called successfully
	 * on the given car before. The same type of exception is also thrown if the departure occurs before the arrival, meaning that the parking {@link Clock}
	 * (passed at its creation) is going backward time. Whatever the reason of the ParkingException, the given car won't be parked anymore at the end of the method.</p>
	 * <p>Method may also involuntary throw or propagate {@link RuntimeException} exceptions maybe thrown by {@link Car#getRegistrationNumber()}, {@link Car#getType()},
	 * {@link Clock#instant()} or {@link PricingPolicy#bill(Car, java.time.Instant, java.time.Instant)}; if that happens, this won't corrupt the internal data of
	 * the parking and the car will either be parked or not parked at the end of the method.</p>
	 * <p>The car is billed according its departure car type. The car type returned by {@link Car#getType()} when leaving the parking may be different than the arrival
	 * car type which was used to find a parking slot. Normally this should not happen as the car is not supposed to be transformed while it is parked, but know that this is
	 * supported. </p>
	 * <p>This method may return and bill an invalid price if the pricing policy passed to the constructor returns an invalid price.</p> 
	 * <p>Default implementation is thread safe.</p>
	 * @see Parking#unparkAndBill(Car)
	 * @see Parking#park(Car)
	 * @param car the car, not null
	 * @return the price the driver of the car has to pay
	 * @throws ParkingException if the car was not parked or if the departure occurs before the arrival according the parking clock
	 */
	float unparkAndBill(Car car) throws ParkingException;
	
	/**
	 * Test whether or not a car is parked.
	 * <p>Only the car registration number is taken into account, meaning that changing the car type returned by {@link Car#getType()} has no incidence
	 * on the result of that method.</p>
	 * <p>Default implementation is thread safe.</p>
	 * @param car the car, not null
	 * @return true if the car is parked, false otherwise
	 */
	boolean isParked(Car car);
}