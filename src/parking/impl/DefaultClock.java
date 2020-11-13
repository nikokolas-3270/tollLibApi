/*
 * Part of the 'Parking toll library'
 */
package parking.impl;

import java.time.Instant;

import parking.api.Clock;

/**
 * The default Clock implementation.
 * <p>This is a real time based clock, meaning that if you:<ul>
 * <li>Call {@link #instant()} method</li>
 * <li>Sleep 2 seconds and then</li>
 * <li>Then call the instant method again</li>
 * </ul>
 * Then the second instant will be older than the first instant by 2 seconds.</p>
 * <p>This default clock is used when calling {@link parking.api.Parking#create(java.util.Map, parking.api.PricingPolicy, Clock)}
 * with a null clock or when calling {@link parking.api.Parking#create(java.util.Map, parking.api.PricingPolicy)}
 */
final class DefaultClock implements Clock {
	/** The unique instance of this class. */
	static final DefaultClock instance = new DefaultClock();
	
	private final java.time.Clock delegate; 
	
	/**
	 * Instantiates a new default clock.
	 */
	private DefaultClock() {
		this.delegate = java.time.Clock.systemUTC();
	}
	
	/**
	 * The current instant.
	 * @return the instant, not null
	 */
	@Override
	public Instant instant() {
		return this.delegate.instant();
	}
}
