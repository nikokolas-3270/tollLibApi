/*
 * Part of the 'Parking toll library'
 */
package parking.test;

import java.time.Instant;
import java.time.temporal.TemporalAmount;

import parking.api.Clock;

/**
 * The mock implementation of Clock.
 * <p>Allow a unittest to change the current instant at will</p>
 */
final class ClockMock implements Clock {
	
	/** The instant. */
	private Instant instant;
	
	/**
	 * Instantiates a new clock mock.
	 */
	ClockMock() {
		this.instant = java.time.Instant.now();
	}

	/**
	 * The current instant.
	 * @return the instant, not null
	 */
	@Override
	public Instant instant() {
		return this.instant;
	}
	
	/**
	 * Sets the current instant.
	 * @param  the new current instant, not null
	 */
	public void setInstant(Instant instant) {
		this.instant = instant;
	}
	
	/**
	 * Increment the current instant
	 * @param amountToAdd the amount to add to the current instant, not null
	 */
	public void increment(TemporalAmount amountToAdd) {
		this.instant = this.instant.plus(amountToAdd);
	}
}
