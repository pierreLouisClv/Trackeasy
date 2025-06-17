package com.trackeasy.app.entities;

/**
 * @author USER
 * @version 1.0
 * @created 13-juin-2025 12:05:45
 */
public class Conductor extends Person {

	public Vehicle m_Vehicle;

    public Conductor() {
        super();
    }

    public Conductor(String personID, String firstname, String lastname, String id) {
        super(personID, firstname, lastname, id);
    }

	public void finalize() throws Throwable {
		super.finalize();
	}
}//end Conductor