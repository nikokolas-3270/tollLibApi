/*
 * Part of the 'Parking toll library'
 */
package parking.api;

/**
 * The Interface Car.
 * <p>Describe the car you want to park or unpark in a {@link Parking} instance.
 * Implement this interface to use the parking API or use the {@link DefaultCar} implementation.</p>
 * @see Parking
 */
public interface Car {
	/**
	 * Gets the registration number.
	 * <p>Uniquely identify a car in a {@link parking.api.Parking} instance. Can be null.</p>
	 * <p>Method may throw a {@link RuntimeException} without affecting the {@link Parking} operations atomicity.</p>
	 * @return the registration number
	 */
	String getRegistrationNumber();
	
	/**
	 * Type of car.
	 * <p>Each enum value condenses the characteristics of the car.</p>
	 */
	enum Type {
		/** Type for cars with a thermal (gasoline or diesel) engine. */
		GASOLINE("Gas"),
		
		/** Type for cars with an electric engine and a 20kW power supply. */
		ELECTRIC_WITH_20kW_POWER_SUPPLY("20 kW"),
		
		/** Type for cars with an electric engine and a 50kW power supply. */
		ELECTRIC_WITH_50kW_POWER_SUPPLY("50 kW");
		
		/** A string briefly describing the type. */
		public final String label;
		
		/**
		 * Instantiates a new type.
		 * @param label the label
		 */
		private Type(String label) {
			this.label = label;
		}
		
		public String toString() {
			return this.label;
		}
	}
	
	/**
	 * Gets the type.
	 * <p>Type is not supposed to change while the car parked (i.e. between {@link Parking#park(Car)} and {@link Parking#unparkAndBill(Car)} calls).
	 * If it does it will be possible to unpark the car but the car will be billed according the new car type (while it was parked on a slot associated to the old car type).</p>
	 * <p>Method may throw a {@link RuntimeException} without affecting the {@link Parking} operations atomicity.</p>
	 * @return the type
	 */
	Type getType();
}
