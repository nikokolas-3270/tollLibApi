/*
 * Part of the 'Parking toll library'
 */
package parking.api;

import java.time.Duration;
import java.time.Instant;

/**
 * A partial implementation of PricingPolicy.
 * <p>Bill a car according the duration it stayed in the parking.</p>
 */
public abstract class DurationPricingPolicy implements PricingPolicy {
	/**
	 * Replaced by and rely on {@link #bill(Duration)}
	 */
	@Override
	public final float bill(Car car, Instant arrivalInstant, Instant departureInstant) {
		return bill(Duration.between(arrivalInstant, departureInstant));
	}
	
	/**
	 * Bill a car according the duration it stayed in the parking.
	 * <p>To be implemented in place of {@link #bill(Car, Instant, Instant)} in subclasses.</p>
	 * <p>Remark that the car to bill is no longer an argument in this method. If not happy with that, please implement {@link PricingPolicy} directly.</p>
	 * <p>Returning an invalid (negative) value or throwing a {@link RuntimeException } only affects the billing of car leaving the parking:
	 * {@link Parking#unparkAndBill(Car)} will release the parking slot (if possible) and propagate the invalid value or the exception.</p>
	 * @param parkingDuration the parking duration, not null, always positive or zero
	 * @return the price to pay to leave the parking
	 */
	public abstract float bill(Duration parkingDuration);
}