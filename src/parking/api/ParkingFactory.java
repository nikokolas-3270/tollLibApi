/*
 * Part of the 'Parking toll library'
 */
package parking.api;

import java.util.Map;

import parking.impl.ParkingFactoryImpl;

/**
 * A factory for creating Parking objects.
 * <p>This factory is used to properly separate the parking API (provided by the {@link Parking} interface) from the implementation or even change the default implementation.
 * <b>Implementing this interface is normally not needed</b> as a default implementation is provided.
 * Also <b>you should not call a factory yourself</b> as creating {@link Parking} instances should be done
 * through {@link Parking#create(Map, PricingPolicy)} or {@link Parking#create(Map, PricingPolicy, Clock)} static methods.
 * <b><font color=red>In other words playing with parking factories is not something you normally want to do.</font></b></p>
 */
public interface ParkingFactory {
	/**
	 * The Class Singleton.
	 * <p>Holds the factory default instance</p>
	 */
	static public final class Singleton {
		static private ParkingFactory instance = ParkingFactoryImpl.instance;
		
		/**
		 * Not to be used
		 */
		private Singleton() {
		}
		
		/**
		 * Get the factory default instance.
		 * @return the default instance
		 */
		static ParkingFactory getInstance() {
			return Singleton.instance;
		}
		
		/**
		 * Register/set the factory default instance.
		 * <p>Doing so will affect the creation of {@link Parking} instances through {@link Parking#create(Map, PricingPolicy)} or
		 * {@link Parking#create(Map, PricingPolicy, Clock)} static methods which is <b>something you normally do not want to do</b>.</p>
		 * @param instance the new default instance, not null
		 * @throws NullPointerException if the given instance is null
		 */
		static public void registerInstance(ParkingFactory instance) {
			if (instance == null) {
				throw new NullPointerException("Cannot register null as singleton instance");
			}
			
			Singleton.instance = instance;
		}
	}
	
	/**
	 * Create a new Parking object.
	 * <p>Static method {@link Parking#create(Map, PricingPolicy, Clock)} delegates its calls to this method on the singleton instance.
	 * Refer to this method for a complete documentation.</p>
	 * @see Parking#create(Map, PricingPolicy, Clock)
	 * @param carTypeToSlotsCount the number of slots for each car type, not null
	 * @param pricingPolicy the pricing policy, not null
	 * @param clock the clock, can be null
	 * @return the parking, not null
	 */
	Parking create(Map<Car.Type, Integer> carTypeToSlotsCount, PricingPolicy pricingPolicy, Clock clock);
}
