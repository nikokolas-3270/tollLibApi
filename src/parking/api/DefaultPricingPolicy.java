/*
 * Part of the 'Parking toll library'
 */
package parking.api;

import java.time.Duration;
import java.time.Instant;

/**
 * The default PricingPolicy implementation.
 * <p>Bill a car according a fixed and a hourly amount.
 * A hour is only due when it is completed.</p>
 */
public final class DefaultPricingPolicy extends DurationPricingPolicy {
	private final float fixedAmount;
	private final float hourlyAmount;
	
	/**
	 * Instantiates a new default pricing policy.
	 * @param fixedAmount the fixed amount
	 * @param hourlyAmount the hourly amount
	 */
	public DefaultPricingPolicy(float fixedAmount, float hourlyAmount) {
		this.fixedAmount = fixedAmount;
		this.hourlyAmount = hourlyAmount;
	}

	/**
	 * Bill a car according the duration it stayed in the parking.
	 * <p>To be implemented in place of {@link #bill(Car, Instant, Instant)} in subclasses.</p>
	 * <p>Amounts passed to the constructor are used to compute the price.
	 * A hour is only due when it is completed, so set the fixed amount to a strictly positive value if you don't want the first parking hour to be free of charge.</p>
	 * <p>Returned value may be invalid (negative) if the amounts passed the constructor are themselves invalid. This only affects the billing:
	 * {@link Parking#unparkAndBill(Car)} will release the parking slot (if possible) and propagate the invalid value.</p>
	 * @param parkingDuration the parking duration, not null, always positive or zero
	 * @return the price to pay to leave the parking
	 */
	@Override
	public float bill(Duration parkingDuration) {
		return this.fixedAmount + (parkingDuration.toHours() * this.hourlyAmount);
	}
}
