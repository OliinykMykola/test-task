package src.service;

import src.dao.AddressBookDao;
import src.model.Person;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides service operations with persons in a database
 */
public class AddressBookService {

    /**
     * String pool
     */

    private final static String MOBILE_CODE = "070";
    private static final String PERSON_NOT_FOUND = "Person not found";

    /**
     * Get an AddressDb instance
     *
     * @see src.dao.AddressBookDao#getInstance
     */
    private final AddressBookDao addressBookDao = AddressBookDao.getInstance();

    /**
     * Add new person entry in a database or update entry,
     * if user with the same name already exist
     *
     * @param person Person object for saving or updating
     * @return {@code true} if success; {@code false} if something went wrong or if {@code person == null}
     */
    public boolean savePerson(Person person) {
        if (addressBookDao.findPerson(person.getName()) == null) {
            try {
                addressBookDao.addPerson(person);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            try {
                addressBookDao.updatePerson(person);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Find entry in a database and return it as new person object
     *
     * @param name The person's name for searching in a database
     * @return {@code new Person} object from database; {@code null}, if person with given name not found
     */
    public Person findPerson(String name) {
        if (name == null) return null;
        return addressBookDao.findPerson(name);
    }

    /**
     * Delete entry from a database
     *
     * @param name The name of the person, which we want to delete from a database
     * @return {@code true} if success; {@code false} if something went wrong
     */
    public boolean deletePerson(String name) {
        if (name == null) return false;
        if (findPerson(name) == null) {
            System.out.println(PERSON_NOT_FOUND);
            return false;
        }
        addressBookDao.deletePerson(name);
        return true;
    }

    /**
     * @param name The person's name for the checking sweden mobile phone availability
     * @return {@code true} if has; {@code false} if doesn't have or if user doesn't found
     */
    public boolean hasMobile(String name) {
        Person person = findPerson(name);
        if (person != null) {
        return person.getPhoneNumber().startsWith(MOBILE_CODE);}
        else {
            return false;
        }
    }


    /**
     * Size of a database with persons
     *
     * @return The number of entries in this database
     */
    public int getSize() {
        return addressBookDao.getAll().size();
    }

    /**
     * Return phone number of the person with given name
     *
     * @param name Name of the person, which telephone number we are looking for
     * @return {@code phoneNumber} of the person; or {@code PERSON_NOT_FOUND} message,
     * if person with given name was not found in a database
     */
    public String getPhoneNumber(String name) {
        Person person = addressBookDao.findPerson(name);
        if (person == null) {
            return PERSON_NOT_FOUND;
        } else return person.getPhoneNumber();
    }

    /**
     * Names of the all persons in a database
     *
     * @param maxLength Max length of the person's name. If name is bigger, it will be cut
     * @return List all person's names in a database
     */
    public List<String> getNames(int maxLength) {
        List<String> names = new ArrayList<>();
        for (Person person : addressBookDao.getAll()) {
            String name = person.getName();
            if (name.length() > maxLength) {
                name = name.substring(0, maxLength);
            }
            names.add(name);
        }
        return names;
    }

    /**
     * @see src.dao.AddressBookDao#getPeopleWithPhone
     */
    public List<Person> getPeopleWithPhone() {
        return addressBookDao.getPeopleWithPhone();
    }

}
