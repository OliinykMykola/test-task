package src.model;

import java.util.Date;

/**
 * Basic application entity
 */
public class Person {

    /**
     * Primary key in a database. Must be unique.
     */
    private String name;

    /**
     * Person's phone number.
     */
    private String phoneNumber;

    /**
     * Date when person was saved or was updated in a database
     */
    private Date date;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Check only name field, because only name is unique identificator. Two persons couldn't have the same name.
     * Probably can be changed.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return name.equals(person.name);
    }

    /**
     * Includes only name field
     */
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date=" + date +
                '}';
    }
}