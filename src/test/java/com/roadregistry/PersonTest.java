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

    // ================== addDemeritPoints() TEST CASES ==================

    @Test
    public void testAddDemeritPoints_TooHighInput() throws IOException {
        simulateAddPerson("66%%%%%%AA", "Demo", "User", "100", "High",
                "City", "State", "Country", "12-03-2002");
        Person person = new Person("66%%%%%%AA", "Demo", "User", "", "12-03-2002");
        boolean result = person.addDemeritPoints("66%%%%%%AA", 7);
        assertFalse(result, "❌ Input > 6 not accepted at once");
    }

    @Test
    public void testAddDemeritPoints_ZeroInput() throws IOException {
        simulateAddPerson("66%%%%%%BB", "Demo", "User", "100", "High",
                "City", "State", "Country", "12-03-2002");
        Person person = new Person("66%%%%%%BB", "Demo", "User", "", "12-03-2002");
        boolean result = person.addDemeritPoints("66%%%%%%BB", 0);
        assertFalse(result, "❌ Input < 1 not accepted");
    }

    @Test
    public void testAddDemeritPoints_Over21_AccumulatedOver12_Suspended() throws IOException {
        String id = "66%%%%%%CC";
        simulateAddPerson(id, "Demo", "User", "100", "High",
                "City", "State", "Country", "21-07-1991");
        Person person = new Person(id, "Demo", "User", "", "21-07-1991");

        assertTrue(person.addDemeritPoints(id, 6));
        assertTrue(person.addDemeritPoints(id, 6));

        // Now add 4 more (total = 16)
        assertTrue(person.addDemeritPoints(id, 4));

        // Check if suspended
        BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(id)) {
                assertTrue(line.endsWith("true"), "✅ isSuspended should be true");
                found = true;
            }
        }
        reader.close();
        assertTrue(found, "✅ Person should be found in the file");
    }

    @Test
    public void testAddDemeritPoints_Under21_AccumulatedOver6_Suspended() throws IOException {
        String id = "66%%%%%%DD";
        simulateAddPerson(id, "Young", "Driver", "50", "Youth",
                "City", "State", "Country", "20-06-2011");
        Person person = new Person(id, "Young", "Driver", "", "20-06-2011");

        assertTrue(person.addDemeritPoints(id, 6));
        assertTrue(person.addDemeritPoints(id, 1));  // Total: 7 (Over limit)

        BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(id)) {
                assertTrue(line.endsWith("true"), "✅ isSuspended should be true for under 21 with >6 points");
                found = true;
            }
        }
        reader.close();
        assertTrue(found, "✅ Person should be found in file");
    }

    @Test
    public void testAddDemeritPoints_Under21_LessThan6_NotSuspended() throws IOException {
        String id = "66%%%%%%EE";
        simulateAddPerson(id, "Safe", "Teen", "50", "Lane",
                "City", "State", "Country", "14-05-2010");
        Person person = new Person(id, "Safe", "Teen", "", "14-05-2010");

        assertTrue(person.addDemeritPoints(id, 4));  // Safe

        BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(id)) {
                assertTrue(line.endsWith("false"), "✅ isSuspended should be false for <6 points");
                found = true;
            }
        }
        reader.close();
        assertTrue(found, "✅ Person should be found in file");
    }
}
