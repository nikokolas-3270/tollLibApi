/*
 * Part of the 'Parking toll library'
 */
package parking.api;

/**
 * The default Car implementation.
 * <p>Default implementation of the {@link Car} interface.
 * Use it if you do not wish to implement the {@link Car} interface yourself.</p>
 */
public final class DefaultCar implements Car {
	private final String registrationNumber;
	private final Type type;
	
	/**
	 * Instantiates a new car.
	 * @param registrationNumber the registration number
	 * @param type the car type
	 */
	public DefaultCar(String registrationNumber, Type type) {
		this.registrationNumber = registrationNumber;
		this.type = type;
	}

	/**
	 * Gets the registration number.
	 * <p>This is the registration number passed to the constructor.</p>
	 * @return the registration number
	 */
	@Override
	public String getRegistrationNumber() {
		return this.registrationNumber;
	}

	/**
	 * Gets the car type.
	 * <p>This is the type passed to the constructor.</p>
	 * @return the car type
	 */
	@Override
	public Type getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return this.registrationNumber + " (" + this.type + ")";
	}
}
