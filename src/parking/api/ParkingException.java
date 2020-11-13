/*
 * Part of the 'Parking toll library'
 */
package parking.api;

/**
 * The library specific exception.
 * <p>Exception used to segregate exceptions intentionally thrown by the parking from the other possible exceptions.</p> 
 * @see Parking
 */
public class ParkingException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new parking exception.
	 * @param message the message
	 */
	public ParkingException(String message) {
		super(message);
	}
}
