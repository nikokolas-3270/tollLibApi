/*
 * Part of the 'Parking toll library'
 */
package parking.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import parking.api.Car;
import parking.api.Clock;
import parking.api.Parking;
import parking.api.ParkingException;
import parking.api.PricingPolicy;

/**
 * The default Parking implementation.
 * <p>This should normally be the only Parking implementation. Refer to the {@link Parking} documentation for more details.</p>
 */
final class ParkingImpl implements Parking {
	
	/**
	 * The Class SlotOccupationInfo.
	 * <p>Stores informations about an occupied parking slot.</p>
	 */
	static private final class SlotOccupationInfo {
		final Car.Type arrivalCarType;
		final String slotId;
		final Instant arrivalInstant;
		
		/**
		 * Instantiates a new slot occupation info.
		 * @param arrivalCarType the arrival car type
		 * @param slotId the slot id, not null
		 * @param arrivalInstant the arrival instant, not null
		 */
		SlotOccupationInfo(Car.Type arrivalCarType, String slotId, Instant arrivalInstant) {
			this.arrivalCarType = arrivalCarType;
			this.slotId = slotId;
			this.arrivalInstant = arrivalInstant;
		}
	}
	
	/* Below mutex protects concurrent accesses on following members
	 *   registrationNumberToSlotOccupationInfo
	 *   carTypeToAvailableSlotIds */
	private Object mutex;
	private Map<String, SlotOccupationInfo> registrationNumberToSlotOccupationInfo;
	private Map<Car.Type, Set<String>> carTypeToAvailableSlotIds;
	
	private final PricingPolicy pricingPolicy;
	private final Clock clock;
	
	/**
	 * Instantiates a new parking implementation.
	 * <p>Refer to {@link Parking#create(Map, PricingPolicy, Clock)} for a complete documentation.</p>
	 * @see Parking#create(Map, PricingPolicy, Clock)
	 * @param carTypeToSlotsCount the number of slots for each car type, not null
	 * @param pricingPolicy the pricing policy, not null
	 * @param clock the clock, can be null
	 */
	ParkingImpl(Map<Car.Type, Integer> carTypeToSlotsCount, PricingPolicy pricingPolicy, Clock clock) {
		this.mutex = new Object();
		this.registrationNumberToSlotOccupationInfo = new HashMap<String, SlotOccupationInfo>();
		this.carTypeToAvailableSlotIds = new HashMap<Car.Type, Set<String>>();
		
		for (Map.Entry<Car.Type, Integer> catTypeAndSlotsCount: carTypeToSlotsCount.entrySet()) {
			Car.Type carType = catTypeAndSlotsCount.getKey();
			String slotIdPrefix = (carType == Car.Type.GASOLINE ? "" : carType + " - ");
			int slotsCount = catTypeAndSlotsCount.getValue().intValue();
			Set<String> availableSlotIds = new HashSet<String>();
			
			for (int slotIndex = 0; slotIndex < slotsCount; slotIndex++) {
				availableSlotIds.add(slotIdPrefix + slotIndex);
			}
			
			this.carTypeToAvailableSlotIds.put(carType, availableSlotIds);
		}
		
		this.pricingPolicy = pricingPolicy;
		this.clock = (clock == null ? DefaultClock.instance : clock);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String park(Car car) throws ParkingException {
		String registrationNumber = car.getRegistrationNumber();
		Car.Type arrivalCarType = car.getType();
		Instant arrivalInstant = this.clock.instant();
		
		synchronized (this.mutex) {
			if (this.registrationNumberToSlotOccupationInfo.containsKey(registrationNumber)) {
				throw new ParkingException("Car with registration number '" + registrationNumber + "' is already parked");
			}
			
			if (!this.carTypeToAvailableSlotIds.containsKey(arrivalCarType)) {
				throw new ParkingException("Parking does not provide slots for cars of type '" + arrivalCarType + "'");
			}
			
			Set<String> availableSlotIds = this.carTypeToAvailableSlotIds.get(arrivalCarType);
			
			if (availableSlotIds.isEmpty()) {
				return null;
			}
			
			String slotId = availableSlotIds.iterator().next();
			
			availableSlotIds.remove(slotId);
			
			this.registrationNumberToSlotOccupationInfo.put(registrationNumber, new SlotOccupationInfo(arrivalCarType, slotId, arrivalInstant));
			
			return slotId;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float unparkAndBill(Car car) throws ParkingException {
		String registrationNumber = car.getRegistrationNumber();
		SlotOccupationInfo slotOccupationInfo = null;
		
		synchronized (this.mutex) {
			if (!this.registrationNumberToSlotOccupationInfo.containsKey(registrationNumber)) {
				throw new ParkingException("Car with registration number '" + registrationNumber + "' has never been parked");
			}
			
			slotOccupationInfo = this.registrationNumberToSlotOccupationInfo.remove(registrationNumber);
			
			assert(this.carTypeToAvailableSlotIds.containsKey(slotOccupationInfo.arrivalCarType));

			this.carTypeToAvailableSlotIds.get(slotOccupationInfo.arrivalCarType).add(slotOccupationInfo.slotId);
		}
		
		Instant arrivalInstant = slotOccupationInfo.arrivalInstant;
		Instant departureInstant = this.clock.instant();
		
		if (departureInstant.compareTo(arrivalInstant) < 0) {
			throw new ParkingException("Unable to bill car with registration number '" + registrationNumber + "' which has just been unparked as the departure instant '" +
				departureInstant + "' is earlier than arrival instant '" + arrivalInstant + "'");
		}
		
		return this.pricingPolicy.bill(car, arrivalInstant, departureInstant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParked(Car car) {
		String registrationNumber = car.getRegistrationNumber();
		
		synchronized (this.mutex) {
			return this.registrationNumberToSlotOccupationInfo.containsKey(registrationNumber);
		}
	}

}
