/*
 * Part of the 'Parking toll library'
 */
package parking.api;

import java.time.Instant;

/**
 * The Interface Clock.
 * <p>Used by a parking to know how much time a car was parked and bill it accordingly.
 * <b>You normally do not have to implement this interface</b> as a {@link Parking} instance can be created by {@link Parking#create(java.util.Map, PricingPolicy)}
 * without providing a clock (a default clock is provided in that case).</p>
 * @see Parking
 */
public interface Clock {
	/**
	 * The current instant.
	 * <p>Returned instant is expected to change and be greater if this method is called again later in time.
	 * Not complying with this rule will cause {@link Parking#unparkAndBill(Car)} method to throw a {@link ParkingException} exception.</p>
	 * @return the instant, not null
	 */
	Instant instant();
}