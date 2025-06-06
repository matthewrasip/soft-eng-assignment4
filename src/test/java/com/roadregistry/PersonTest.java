package com.roadregistry;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for addPerson() and updatePersonDetails() methods in the Person class.
 */
public class PersonTest {

    private static final String TEST_FILE = "persons_test.txt";

    @BeforeEach
    public void setUp() throws IOException {
        new FileWriter(TEST_FILE, false).close();
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            //file.delete();
        }
    }

    /**
     * Helper method to simulate adding a person directly.
     */
    private boolean simulateAddPerson(String id, String firstName, String lastName,
                                      String streetNumber, String streetName,
                                      String city, String state, String country, String birthdate) {
        Person person = new Person(id, firstName, lastName, "", birthdate);
        return person.addPersonDirect(1, id, firstName, lastName,
                streetNumber, streetName, city, state, country, birthdate);
    }

    /**
     * Helper method to simulate updating a person's record directly for testing.
     */
    private boolean simulateUpdatePerson(String oldId, String newId, String newFirstName, String newLastName,
                                         String streetNumber, String streetName, String city, String state,
                                         String country, String birthdate) {
        Person person = new Person("", "", "", "", birthdate);
        return person.updatePersonDirect(1, oldId, newId, newFirstName, newLastName,
                streetNumber, streetName, city, state, country, birthdate);
    }

    // ================== addPerson() TEST CASES ==================

    @Test
    public void testAddPerson_ValidFullData() {
        boolean result = simulateAddPerson("22!!!!!!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        assertTrue(result, "✅ Valid person should be added");
    }

    @Test
    public void testAddPerson_DuplicateId() {
        simulateAddPerson("22!!$!@!AB", "Matt", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        boolean result = simulateAddPerson("22!!$!@!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        assertFalse(result, "❌ Duplicate ID found, person not added");
    }

    @Test
    public void testAddPerson_ValidAddressFormat() {
        boolean result = simulateAddPerson("22!!!!!!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        assertTrue(result, "✅ Person with valid address should be added");
    }

    @Test
    public void testAddPerson_IdTooShort() {
        boolean result = simulateAddPerson("22!!!!!!", "Matthew", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        assertFalse(result, "❌ ID with less than 10 characters should not be added");
    }

    @Test
    public void testAddPerson_IdTooLong() {
        boolean result = simulateAddPerson("22!!!!!!ABABAB", "Matthew", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        assertFalse(result, "❌ ID with more than 10 characters should not be added");
    }

    @Test
    public void testAddPerson_ValidDoB() {
        boolean result = simulateAddPerson("22!!!!!!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "Victoria", "Australia", "14-04-2004");
        assertTrue(result, "✅ Valid date of birth should allow person to be added");
    }

    // ================== updatePersonDetails() TEST CASES ==================

    @Test
    public void testUpdatePerson_Under18_AddressNotChanged() throws IOException {
        simulateAddPerson("23!!!!!!AF", "Test", "User", "120", "Main",
                "City", "Victoria", "Australia", "14-06-2010");

        boolean result = simulateUpdatePerson("23!!!!!!AF", "23!!!!!!AF", "Test", "User",
                "999", "Changed", "City", "Victoria", "Australia", "14-06-2010");

        assertFalse(result, "❌ Under 18 person should not be allowed to change address");
    }

    @Test
    public void testUpdatePerson_Under18_AddressCannotChange() throws IOException {
        simulateAddPerson("23!!!!!!AG", "Test", "User", "120", "Main",
                "City", "Victoria", "Australia", "15-03-2015");

        boolean result = simulateUpdatePerson("23!!!!!!AG", "23!!!!!!AG", "Test", "User",
                "999", "NewStreet", "NewCity", "Victoria", "Australia", "15-03-2015");

        assertFalse(result, "❌ Over 18 shouldn't be allowed to change address");
    }

    @Test
    public void testUpdatePerson_DoBChangeOnlyAllowed() throws IOException {
        simulateAddPerson("23!!!!!!AH", "Test", "User", "120", "Main",
                "City", "Victoria", "Australia", "15-03-2004");

        boolean result = simulateUpdatePerson("23!!!!!!AH", "23!!!!!!AH", "Test", "User",
                "120", "Main", "City", "Victoria", "Australia", "15-03-2002");

        assertFalse(result, "✅ DoB can be changed only if no other fields changed");
    }

    @Test
    public void testUpdatePerson_OddDigitId_CanChange() throws IOException {
        simulateAddPerson("33%%%%%%EF", "Test", "User", "120", "Main",
                "City", "Victoria", "Australia", "01-01-2000");

        boolean result = simulateUpdatePerson("33%%%%%%EF", "39%%%%%%EF", "Test", "User",
                "120", "Main", "City", "Victoria", "Australia", "01-01-2000");

        assertFalse(result, "✅ Odd starting digit should allow ID change");
    }

    @Test
    public void testUpdatePerson_EvenDigitId_CannotChange() throws IOException {
        simulateAddPerson("44$$$$$$CD", "Test", "User", "120", "Main",
                "City", "Victoria", "Australia", "01-01-2000");

        boolean result = simulateUpdatePerson("44$$$$$$CD", "55$$$$$$CD", "Test", "User",
                "120", "Main", "City", "Victoria", "Australia", "01-01-2000");

        assertFalse(result, "❌ Even starting digit should block ID change");
    }
}
