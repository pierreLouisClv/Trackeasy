package com.trackeasy.app.entities;

/**
 * @author USER
 * @version 1.0
 * @created 13-juin-2025 12:05:54
 */
public class Person {

    private String personID;  // Clé primaire
    private String firstname;
    private String lastname;
    private String id;        // Peut être une pièce d'identité ou similaire

    public Person() {
    }

    public Person(String personID, String firstname, String lastname, String id) {
        this.personID = personID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.id = id;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
