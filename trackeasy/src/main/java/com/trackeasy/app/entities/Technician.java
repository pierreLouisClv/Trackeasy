package com.trackeasy.app.entities;

/**
 * @author USER
 * @version 1.0
 * @created 13-juin-2025 12:05:54
 */
public class Technician extends Person {

	public Tracker m_Tracker;

	public Technician(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}
	/**
	 * 
	 * @param int
	 */
	public boolean installTracker(String trackerID, String type, String vehicleID) {
        return VehicleDAO.installTracker(trackerID, type, getPersonID(), vehicleID);
    }
}//end Technician