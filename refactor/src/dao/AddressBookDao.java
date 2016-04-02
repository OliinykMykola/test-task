package src.dao;

import src.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton data access class. Provides basic CRUD operations with persons in a database.
 */
public class AddressBookDao {

    /**
     * String pool
     */

    /**
     * For using new database just upload new driver and change this fields.
     */
    private static final String DB_CONNECTION_URL = "jdbc:oracle:thin:@prod";
    private static final String DB_CONNECTION_USERNAME = "admin";
    private static final String DB_CONNECTION_PASSWORD = "beefhead";
    private static final String JDBC_DRIVER = "oracle.jdbc.ThinDriver";

    /**
     * Change this fields, if you use different database table columns
     */
    private static final String NAME_COLUMN = "name";
    private static final String PHONE_NUMBER_COLUMN = "phoneNumber";
    private static final String DATE_COLUMN = "date";

    /**
     * Change this fields, if you need to change or customize SQL dialect.
     */
    private static final String FIND_PERSON = "SELECT * FROM addressentry WHERE name = ?";
    private static final String GET_ALL_PEOPLE = "SELECT * FROM addressentry";
    private static final String GET_PEOPLE_WITH_MOBILE_PHONE = "SELECT * FROM addressentry WHERE phoneNumber LIKE '070%'";
    private static final String ADD_PERSON = "INSERT INTO addressentry (name, phoneNumber, date) VALUES (?, ?, ?)";
    private static final String UPDATE_PERSON = "UPDATE addressentry SET phoneNumber = ?, date = ? WHERE name = ?";
    private static final String DELETE_PERSON = "DELETE FROM addressentry WHERE name = ?";

    private static final String CLOSING_ERROR = "Can't close!";
    private static final String DRIVER_ERROR = "Some problems with driver loading. Reason: ";
    private static final String GET_CONNECTION_ERROR = "Some problems with getting database connection. Reason: ";

    private static AddressBookDao instance;

    /**
     * Method to get AddressDb instance
     *
     * @return Singleton based instance of a AddressDB
     */
    public static AddressBookDao getInstance() {
        if (instance == null) {
            instance = new AddressBookDao();
        }
        return instance;
    }

    /**
     * Basic constructor. Load JDBC Driver class in a memory
     */
    private AddressBookDao() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println(DRIVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Getting connection with a database
     *
     * @return New Connection; {@code null} if something went wrong
     */
    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_CONNECTION_URL, DB_CONNECTION_USERNAME, DB_CONNECTION_PASSWORD);
        } catch (SQLException e) {
            System.err.println(GET_CONNECTION_ERROR);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save new person in a database
     *
     * @param person Person object to save in a database
     * @throws SQLException
     */
    public void addPerson(Person person) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(ADD_PERSON);
            statement.setString(1, person.getName());
            statement.setString(2, person.getPhoneNumber());
            statement.setDate(3, new Date(person.getDate().getTime()));
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            close(statement);
            close(connection);
        }
    }

    /**
     * Update person entry in a database
     *
     * @param person Person object to update in a database
     * @throws SQLException
     */
    public void updatePerson(Person person) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(UPDATE_PERSON);
            statement.setString(1, person.getPhoneNumber());
            statement.setDate(2, new Date(person.getDate().getTime()));
            statement.setString(3, person.getName());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            close(statement);
            close(connection);
        }
    }

    /**
     * Delete entry from a database
     *
     * @param name Name of the person, which we want to delete from a database
     */
    public void deletePerson(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(DELETE_PERSON);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    /**
     * Find the person with given name
     *
     * @param name Name of the person that we are looking for
     * @return Person with given name; {@code null} if not found
     */
    public Person findPerson(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(FIND_PERSON);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? getPersonFromResultSet(resultSet) : null;
        } catch (SQLException e) {
            e.printStackTrace();
            close(statement);
            close(connection);
            return null;
            }
        }

    /**
     * List all persons in a database
     *
     * @return All people saved in a database
     */
    public List<Person> getAll() {
        List<Person> persons = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(GET_ALL_PEOPLE);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                persons.add(getPersonFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return persons;
    }

    /**
     * List all persons in a database, who have sweden mobile phone
     *
     * @return All people with a sweden mobile phone
     */
    public List<Person> getPeopleWithPhone() {
        List<Person> persons = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(GET_PEOPLE_WITH_MOBILE_PHONE);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                persons.add(getPersonFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return persons;
    }

    /**
     * Create person object from given ResultSet
     *
     * @return New person object
     * @throws SQLException
     */
    private Person getPersonFromResultSet(ResultSet resultSet) throws SQLException {
        Person person = new Person();
        person.setName(resultSet.getString(NAME_COLUMN));
        person.setPhoneNumber(resultSet.getString(PHONE_NUMBER_COLUMN));
        person.setDate(resultSet.getDate(DATE_COLUMN));
        return person;
    }

    /**
     * Close given statement if is not null
     */
    private void close(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            System.err.println(CLOSING_ERROR + st);
        }
    }

    /**
     * Close given connection if is not null
     */
    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println(CLOSING_ERROR + connection);
        }
    }
}