/*
 * Part of the 'Parking toll library'
 */
package parking.api;

import java.time.Instant;

/**
 * The Interface PricingPolicy.
 * <p>Used by a {@link Parking} instance to bill a car when it leaves the parking.
 * You have to implement this interface to provide your own bill algorithm when creating a parking.
 * Take a look at {@link DefaultPricingPolicy} for a default pricing policy.</p>
 * @see Parking
 * @see DefaultPricingPolicy
 */
public interface PricingPolicy {
	/**
	 * Bill a car according its arrival and departure instant.
	 * <p>You can return whatever value you want. No check is performed on the validity of the value; typically the value may be negative.
	 * {@link Parking#unparkAndBill(Car)} will return an invalid value if this method returns an invalid value.
	 * Returning an invalid value has no consequence on releasing the parking spot.</p> 
	 * @param car the car to bill
	 * @param arrivalInstant the arrival instant, not null, before the departure instant
	 * @param departureInstant the departure instant, not null, after the arrival instant
	 * @return the price to pay to leave the parking
	 */
	float bill(Car car, Instant arrivalInstant, Instant departureInstant);
}
