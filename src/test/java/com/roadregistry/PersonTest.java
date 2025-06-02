package com.roadregistry;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for addPerson() method in the Person class.
 */
public class PersonTest {

    private static final String TEST_FILE = "persons_test.txt"; // Make sure test uses same file or redirect path

    @BeforeEach
    public void setUp() throws IOException {
        // Clear the test file before each test
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
     * Helper method to simulate adding a person directly (bypassing Scanner input).
     */
    private boolean simulateAddPerson(String id, String firstName, String lastName,
                                      String streetNumber, String streetName,
                                      String city, String state, String country, String birthdate) {
        Person person = new Person(id, firstName, lastName, "", birthdate);
        return person.addPersonDirect(id, firstName, lastName,
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
}
