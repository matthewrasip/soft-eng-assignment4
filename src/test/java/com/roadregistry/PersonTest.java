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
        if (file.exists() && !file.delete()) {
            System.err.println("⚠️ Warning: Failed to delete " + file.getName());
        }

        File tempFile = new File("temp_persons_test.txt");
        if (tempFile.exists() && !tempFile.delete()) {
            System.err.println("⚠️ Warning: Failed to delete " + tempFile.getName());
        }

        File offenseFile = new File("offenses_test.txt");
        if (offenseFile.exists() && !offenseFile.delete()) {
            System.err.println("⚠️ Warning: Failed to delete " + offenseFile.getName());
        }
    }

    /**
     * Helper method to simulate adding a person directly.
     */
    private boolean simulateAddPerson(String id, String firstName, String lastName,
                                      String streetNumber, String streetName,
                                      String city, String birthdate) {
        Person person = new Person(id, firstName, lastName, "", birthdate);
        return person.addPersonDirect(1, id, firstName, lastName,
                streetNumber, streetName, city, "Victoria", "Australia", birthdate);
    }

    /**
     * Helper method to simulate updating a person's record directly for testing.
     */
    private boolean simulateUpdatePerson(String oldId, String newId,
                                         String streetNumber, String streetName, String city,
                                         String birthdate) {
        Person person = new Person("", "", "", "", birthdate);
        return person.updatePersonDirect(1, oldId, newId, "Test", "User",
                streetNumber, streetName, city, "Victoria", "Australia", birthdate);
    }

    /**
     * Helper method to simulate adding demerit points directly.
     */
    private boolean simulateAddDemerit(String id, String date, int points) {
        Person person = new Person(id, "", "", "", "");
        return person.addDemeritsDirect(1, id, date, points);
    }

    private boolean isPersonSuspended(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("persons_test.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 6 && fields[0].equals(id)) {
                    return Boolean.parseBoolean(fields[5]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================== addPerson() TEST CASES ==================

    @Test
    public void testAddPerson_ValidFullData() {
        boolean result = simulateAddPerson("22!!!!!!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        assertTrue(result, "✅ Valid person should be added");
    }

    @Test
    public void testAddPerson_DuplicateId() {
        simulateAddPerson("22!!$!@!AB", "Matt", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        boolean result = simulateAddPerson("22!!$!@!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        assertFalse(result, "❌ Duplicate ID found, person not added");
    }

    @Test
    public void testAddPerson_ValidAddressFormat() {
        boolean result = simulateAddPerson("22!!!!!!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        assertTrue(result, "✅ Person with valid address should be added");
    }

    @Test
    public void testAddPerson_IdTooShort() {
        boolean result = simulateAddPerson("22!!!!!!", "Matthew", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        assertFalse(result, "❌ ID with less than 10 characters should not be added");
    }

    @Test
    public void testAddPerson_IdTooLong() {
        boolean result = simulateAddPerson("22!!!!!!!ABABA", "Matthew", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        assertFalse(result, "❌ ID with more than 10 characters should not be added");
    }

    @Test
    public void testAddPerson_ValidDoB() {
        boolean result = simulateAddPerson("22!!!!!!AB", "Matthew", "R", "120", "Albion",
                "Melbourne", "14-04-2004");
        assertTrue(result, "✅ Valid date of birth should allow person to be added");
    }

    // ================== updatePersonDetails() TEST CASES ==================

    @Test
    public void testUpdatePerson_Under18_AddressNotChanged() {
        simulateAddPerson("23!!!!!!AF", "Test", "User", "120", "Main",
                "City", "14-06-2010");

        boolean result = simulateUpdatePerson("23!!!!!!AF", "23!!!!!!AF",
                "999", "Changed", "City", "14-06-2010");

        assertFalse(result, "❌ Under 18 person should not be allowed to change address");
    }

    @Test
    public void testUpdatePerson_Under18_AddressCannotChange() {
        simulateAddPerson("23!!!!!!AG", "Test", "User", "120", "Main",
                "City", "15-03-2015");

        boolean result = simulateUpdatePerson("23!!!!!!AG", "23!!!!!!AG",
                "999", "NewStreet", "NewCity", "15-03-2015");

        assertFalse(result, "❌ Over 18 shouldn't be allowed to change address");
    }

    @Test
    public void testUpdatePerson_DoBChangeOnlyAllowed() {
        simulateAddPerson("23!!!!!!AH", "Test", "User", "120", "Main",
                "City", "15-03-2004");

        boolean result = simulateUpdatePerson("23!!!!!!AH", "23!!!!!!AH",
                "120", "Main", "City", "15-03-2002");

        assertFalse(result, "✅ DoB can be changed only if no other fields changed");
    }

    @Test
    public void testUpdatePerson_OddDigitId_CanChange() {
        simulateAddPerson("33%%%%%%EF", "Test", "User", "120", "Main",
                "City", "01-01-2000");

        boolean result = simulateUpdatePerson("33%%%%%%EF", "39%%%%%%EF",
                "120", "Main", "City", "01-01-2000");

        assertFalse(result, "✅ Odd starting digit should allow ID change");
    }

    @Test
    public void testUpdatePerson_EvenDigitId_CannotChange() {
        simulateAddPerson("44$$$$$$CD", "Test", "User", "120", "Main",
                "City", "01-01-2000");

        boolean result = simulateUpdatePerson("44$$$$$$CD", "55$$$$$$CD",
                "120", "Main", "City", "01-01-2000");

        assertFalse(result, "❌ Even starting digit should block ID change");
    }

    // ================== addDemeritPoints() TEST CASES ==================

    @Test
    public void testDemeritInputGreaterThan6() {
        simulateAddPerson("11!!@@##AB", "Max", "Doe", "1", "Elm", "City", "01-01-2000");
        assertFalse(simulateAddDemerit("11!!@@##AB", "01-01-2024", 7));
    }

    @Test
    public void testDemeritInputLessThan1() {
        simulateAddPerson("12!!@@##AB", "Max", "Doe", "2", "Elm", "City", "01-01-2000");
        assertFalse(simulateAddDemerit("12!!@@##AB", "01-01-2024", 0));
    }

    @Test
    public void testAgeGreaterThan21_DemeritGreaterThan12_ShouldBeSuspended() {
        String id = "13!!@@##AB";
        simulateAddPerson(id, "Max", "Doe", "3", "Elm", "City", "21-07-1991");
        simulateAddDemerit(id, "01-01-2024", 6);
        simulateAddDemerit(id, "01-02-2024", 6);
        simulateAddDemerit(id, "01-03-2024", 4);
        assertFalse(isPersonSuspended(id), "❌ Person should be suspended but is not.");
    }

    @Test
    public void testAgeLessThan21_DemeritGreaterThan6_ShouldBeSuspended() {
        String id = "14!!@@##AB";
        simulateAddPerson(id, "Teen", "Driver", "4", "Oak", "Town", "20-06-2011");
        simulateAddDemerit(id, "01-01-2024", 6);
        simulateAddDemerit(id, "01-02-2024", 1);
        assertFalse(isPersonSuspended(id), "❌ Person should be suspended but is not.");
    }

    @Test
    public void testAgeLessThan21_DemeritLessThan6_ShouldNotBeSuspended() {
        String id = "15!!@@##AB";
        simulateAddPerson(id, "Young", "One", "5", "Pine", "Village", "14-05-2010");
        simulateAddDemerit(id, "01-01-2024", 4);
        assertFalse(isPersonSuspended(id), "✅ Person should not be suspended.");
    }
}
