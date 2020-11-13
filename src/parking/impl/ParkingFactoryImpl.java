/*
 * Part of the 'Parking toll library'
 */
package parking.impl;

import java.util.Map;

import parking.api.Car.Type;
import parking.api.Clock;
import parking.api.Parking;
import parking.api.ParkingFactory;
import parking.api.PricingPolicy;

/**
 * The default factory for creating Parking objects.
 * <p>This factory is used to properly separate the parking API (provided by the {@link Parking} interface) from the implementation or even change the default implementation.</p>
 * <p>Creates {@link ParkingImpl} objects.</p>
 */
public final class ParkingFactoryImpl implements ParkingFactory {
	/** The unique  instance. */
	static public final ParkingFactoryImpl instance = new ParkingFactoryImpl();
	
	/**
	 * Instantiates a new parking factory impl.
	 */
	private ParkingFactoryImpl() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Parking create(Map<Type, Integer> carTypeToSlotsCount, PricingPolicy pricingPolicy, Clock clock) {
		return new ParkingImpl(carTypeToSlotsCount, pricingPolicy, clock);
	}
}
