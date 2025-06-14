package com.trackeasy.app.entities;

/**
 * @author USER
 * @version 1.0
 * @created 13-juin-2025 12:05:54
 */
public class Person {

	private char firstName;
	private int id;
	private char lastName;

	public Person(){

	}

	public void finalize() throws Throwable {

	}
	/**
	 * 
	 * @param id
	 */
	public char getName(int id){
		return 0;
	}
	public String getPersonID() {
        return id;
    }
	public void setPersonID(String personID) {
        this.id = personID;
    }
}//end Person