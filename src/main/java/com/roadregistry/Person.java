package com.roadregistry;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

/**
 * Represents a person in the RoadRegistry system.
 * Supports adding new person records, updating personal details,
 * and adding demerit points using a text file for storage.
 */
// runMode 0 = Normal run. runMode 1 = Test mode
public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private final boolean isSuspended;

    public Person(String id, String firstName, String lastName, String address, String birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.isSuspended = false;
    }

    /**
     * Appends a person's record to persons.txt file.
     */
    public void addPerson() {
        Scanner scanner = new Scanner(System.in);

        // Person ID
        while (true) {
            System.out.print("Enter person ID (10 characters, special format required): ");
            this.id = scanner.nextLine().trim();
            if (isValidId("persons.txt", id)) break;
            System.out.println("❌ Invalid ID format.\n\tRules:\n\t- ID must not be duplicate.\n\t- Exactly 10 characters long.\n\t- The first two characters must be digits between 2 and 9.\n\t- Characters 3 to 8 must include at least 2 special characters.\n\t- The last two characters must be uppercase English letters (A–Z).\n");
        }

        // First Name
        while (true) {
            System.out.print("Enter first name: ");
            this.firstName = scanner.nextLine().trim();
            if (firstName.matches("[A-Za-z]+")) break;
            System.out.println("❌ Invalid first name. Only letters allowed.");
        }

        // Last Name
        while (true) {
            System.out.print("Enter last name: ");
            this.lastName = scanner.nextLine().trim();
            if (lastName.matches("[A-Za-z]+")) break;
            System.out.println("❌ Invalid last name. Only letters allowed.");
        }

        // Street Number (e.g., 32)
        String streetNumber;
        while (true) {
            System.out.print("Enter street number: ");
            streetNumber = scanner.nextLine().trim();
            if (streetNumber.matches("\\d+")) break;
            System.out.println("❌ Invalid street number. Only digits allowed.");
        }

        // Street Name (e.g., Highland Street)
        String streetName;
        while (true) {
            System.out.print("Enter street name: ");
            streetName = scanner.nextLine().trim();
            if (streetName.matches("[A-Za-z ]+")) break;
            System.out.println("❌ Invalid street name. Only letters and spaces allowed.");
        }

        // City (e.g., Melbourne)
        String city;
        while (true) {
            System.out.print("Enter city: ");
            city = scanner.nextLine().trim();
            if (city.matches("[A-Za-z ]+")) break;
            System.out.println("❌ Invalid city. Only letters and spaces allowed.");
        }

        // State (must be Victoria)
        String state;
        while (true) {
            System.out.print("Enter state (must be Victoria): ");
            state = scanner.nextLine().trim();
            if (state.equalsIgnoreCase("Victoria")) break;
            System.out.println("❌ State must be Victoria.");
        }

        // Country (e.g., Australia)
        String country;
        while (true) {
            System.out.print("Enter country: ");
            country = scanner.nextLine().trim();
            if (country.matches("[A-Za-z ]+")) break;
            System.out.println("❌ Invalid country. Only letters and spaces allowed.");
        }

        // Combine address
        this.address = streetNumber + "|" + streetName + "|" + city + "|" + state + "|" + country;

        // Birthdate (DD-MM-YYYY)
        while (true) {
            System.out.print("Enter birthdate (DD-MM-YYYY): ");
            this.birthdate = scanner.nextLine().trim();
            if (isValidBirthdate(birthdate)) break;
            System.out.println("❌ Invalid birthdate format. Must be DD-MM-YYYY.");
        }

        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(id + "," + firstName + "," + lastName + "," + address + "," + birthdate + "," + isSuspended + "\n");
            System.out.println("✅ Person added successfully.");
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Updates the personal details (name and age) of a person by ID.
     * Reads from "persons.txt", updates the matching line, and writes it back.
     */
    public void updatePersonDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the person you want to update: ");
        String targetId = scanner.nextLine().trim();

        File inputFile = new File("persons.txt");
        File tempFile = new File("persons_temp.txt");

        boolean found = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields;
                fields = line.split(",");
                if (fields.length != 6) {
                    writer.write(line + "\n");
                    continue;
                }

                if (!fields[0].equals(targetId)) {
                    writer.write(line + "\n");
                    continue;
                }

                found = true;
                System.out.println("✅ Person found. Proceeding to update...");

                String originalId = fields[0];
                String originalFirstName = fields[1];
                String originalLastName = fields[2];
                String originalAddress = fields[3];
                String originalBirthdate = fields[4];
                String isSuspended = fields[5];

                String newId = originalId;
                String newFirstName = originalFirstName;
                String newLastName = originalLastName;
                String newAddress = originalAddress;
                String newBirthdate = originalBirthdate;

                // === Calculate age ===
                LocalDate birth = LocalDate.parse(originalBirthdate, formatter);
                int age = getAgeFromBirthdate(String.valueOf(birth));

                boolean hasOtherChanges = false;

                // === ID Update (check even digit restriction) ===
                char firstDigit = originalId.charAt(0);
                String input;
                if (Character.isDigit(firstDigit) && (firstDigit - '0') % 2 == 0) {
                    System.out.println("❌ ID cannot be changed. First digit is even.");
                } else {
                    System.out.print("Enter new ID (or press Enter to keep): ");
                    input = scanner.nextLine().trim();

                    if (!input.isEmpty()) {
                        if (isValidId("persons.txt", input)) {
                            newId = input;
                            hasOtherChanges = true;
                        } else {
                            System.out.println("❌ Invalid ID format. Keeping existing ID.");
                        }
                    }
                }

                // === First Name ===
                System.out.print("Enter new first name (or press Enter to keep): ");
                input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    if (input.matches("[A-Za-z]+")) {
                        newFirstName = input;
                        hasOtherChanges = true;
                    } else {
                        System.out.println("❌ Invalid first name. Keeping existing.");
                    }
                }

                // === Last Name ===
                System.out.print("Enter new last name (or press Enter to keep): ");
                input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    if (input.matches("[A-Za-z]+")) {
                        newLastName = input;
                        hasOtherChanges = true;
                    } else {
                        System.out.println("❌ Invalid last name. Keeping existing.");
                    }
                }

                // === Address Update ===
                if (age < 18) {
                    System.out.println("ℹ️ Under 18: Address cannot be changed.");
                    System.out.println("📍 Current Address: " + originalAddress);
                } else {
                    String[] addr = originalAddress.split("\\|");
                    String streetNumber = addr[0], streetName = addr[1], city = addr[2], state = addr[3], country = addr[4];

                    System.out.print("Enter new street number (or press Enter to keep): ");
                    input = scanner.nextLine().trim();
                    if (!input.isEmpty() && input.matches("\\d+")) {
                        streetNumber = input;
                        hasOtherChanges = true;
                    }

                    System.out.print("Enter new street name (or press Enter to keep): ");
                    input = scanner.nextLine().trim();
                    if (!input.isEmpty() && input.matches("[A-Za-z ]+")) {
                        streetName = input;
                        hasOtherChanges = true;
                    }

                    System.out.print("Enter new city (or press Enter to keep): ");
                    input = scanner.nextLine().trim();
                    if (!input.isEmpty() && input.matches("[A-Za-z ]+")) {
                        city = input;
                        hasOtherChanges = true;
                    }

                    System.out.print("Enter new state (must be Victoria, or press Enter to keep): ");
                    input = scanner.nextLine().trim();
                    if (input.equalsIgnoreCase("Victoria")) {
                        state = input;
                        hasOtherChanges = true;
                    }

                    System.out.print("Enter new country (or press Enter to keep): ");
                    input = scanner.nextLine().trim();
                    if (!input.isEmpty() && input.matches("[A-Za-z ]+")) {
                        country = input;
                        hasOtherChanges = true;
                    }

                    newAddress = streetNumber + "|" + streetName + "|" + city + "|" + state + "|" + country;
                }

                // === Birthdate ===
                if (hasOtherChanges) {
                    System.out.println("ℹ️ Other details have been changed. Birthdate cannot be updated.");
                } else {
                    System.out.print("Enter new birthdate (DD-MM-YYYY) or press Enter to keep: ");
                    input = scanner.nextLine().trim();
                    if (!input.isEmpty()) {
                        if (isValidBirthdate(input)) newBirthdate = input;
                        else System.out.println("❌ Invalid birthdate format. Keeping existing.");
                    }
                }

                // Write the updated record
                writer.write(newId + "," + newFirstName + "," + newLastName + "," + newAddress + "," +
                        newBirthdate + "," + isSuspended + "\n");
            }

            if (!found) {
                System.out.println("❌ Person not found.");
                return;
            }

        } catch (IOException | DateTimeParseException e) {
            System.out.println("❌ Error updating person: " + e.getMessage());
            return;
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println("❌ Could not finalize update.");
            return;
        }
        System.out.println("✅ Update successful.");
    }

    /**
     * Adds demerit points to a person's record identified by ID.
     * Reads from "persons.txt", updates the matching line, and writes it back.
     */
    public void addDemeritPoints() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter person ID: ");
        String personId = scanner.nextLine().trim();

        File inputFile = new File("persons.txt");
        File offenseFile = new File("offenses.txt");

        String matchedLine = null;

        // Load person record
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 6 && fields[0].equals(personId)) {
                    matchedLine = line;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading persons file.");
            return;
        }

        if (matchedLine == null) {
            System.out.println("❌ Person not found.");
            return;
        }

        String[] fields = matchedLine.split(",");
        String birthdate = fields[4];
        boolean suspended = Boolean.parseBoolean(fields[5]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birth = LocalDate.parse(birthdate, formatter);

        // === Offense date ===
        System.out.print("Enter offense date (DD-MM-YYYY): ");
        String offenseDateStr = scanner.nextLine().trim();
        LocalDate offenseDate;

        try {
            offenseDate = LocalDate.parse(offenseDateStr, formatter);
            if (offenseDate.isAfter(LocalDate.now())) {
                System.out.println("❌ Offense date cannot be in the future.");
                return;
            }
            if (offenseDate.isBefore(birth)) {
                System.out.println("❌ Offense date cannot be before birthdate.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format.");
            return;
        }

        // === Points ===
        System.out.print("Enter number of demerit points (1–6): ");
        int pointsToAdd;
        try {
            pointsToAdd = Integer.parseInt(scanner.nextLine().trim());
            if (pointsToAdd < 1 || pointsToAdd > 6) {
                System.out.println("❌ Points must be between 1 and 6.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number.");
            return;
        }

        // === Calculate total points from last 2 years ===
        int recentTotal = pointsToAdd; // include current one
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);

        try (BufferedReader offenseReader = new BufferedReader(new FileReader(offenseFile))) {
            recentTotal = getRecentTotal(personId, formatter, recentTotal, twoYearsAgo, offenseReader);
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println("❌ Error reading offense history.");
            return;
        }

        // === Log to offenses.txt ===
        try (FileWriter offenseWriter = new FileWriter(offenseFile, true)) {
            offenseWriter.write(personId + "," + offenseDateStr + "," + pointsToAdd + "\n");
        } catch (IOException e) {
            System.out.println("❌ Error logging offense.");
            return;
        }

        // === Determine if suspended ===
        int age = Period.between(birth, LocalDate.now()).getYears();
        if ((age < 21 && recentTotal > 6) || (age >= 21 && recentTotal > 12)) {
            suspended = true;
        }

        // === Update persons.txt ===
        File tempFile = new File("persons_temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] rec = line.split(",");
                if (rec.length == 6 && rec[0].equals(personId)) {
                    rec[5] = String.valueOf(suspended); // update suspension
                    writer.write(String.join(",", rec) + "\n");
                    updated = true;
                } else {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Error updating person.");
            return;
        }

        if (updated) {
            if (!inputFile.delete()) {
                System.out.println("⚠️ Warning: failed to delete original file.");
            }
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("⚠️ Warning: failed to rename temp file.");
            }
            System.out.println("✅ Demerit points added. Points (past two years): " + recentTotal);
            if (suspended) System.out.println("⚠️ Person is now suspended.");
        } else {
            if (!tempFile.delete()) {
                System.out.println("⚠️ Warning: Failed to delete temp file.");
            }
            System.out.println("❌ Update failed.");
        }
    }

    private int getRecentTotal(String personId, DateTimeFormatter formatter, int recentTotal, LocalDate twoYearsAgo, BufferedReader offenseReader) throws IOException {
        String line;
        while ((line = offenseReader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length != 3) continue;
            if (!parts[0].equals(personId)) continue;

            LocalDate date = LocalDate.parse(parts[1], formatter);
            if (!date.isBefore(twoYearsAgo)) {
                recentTotal += Integer.parseInt(parts[2]);
            }
        }
        return recentTotal;
    }

    private boolean isValidId(String FILE, String id) {
        if (id.length() != 10) return false;

        // Check first 2 characters: digits between 2–9
        if (!Character.isDigit(id.charAt(0)) || !Character.isDigit(id.charAt(1))) return false;
        int firstDigit = id.charAt(0) - '0';
        int secondDigit = id.charAt(1) - '0';
        if (firstDigit < 2 || firstDigit > 9 || secondDigit < 2 || secondDigit > 9) return false;

        // Check last 2 characters: uppercase letters A-Z
        char secondLast = id.charAt(8);
        char last = id.charAt(9);
        if (!Character.isUpperCase(secondLast) || !Character.isUpperCase(last)) return false;

        // Check characters 3–8 (index 2–7): must include at least 2 special characters
        String middle = id.substring(2, 8);
        int specialCount = 0;
        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) specialCount++;
        }
        if (specialCount < 2) return false;

        // Check for duplicate ID in persons.txt
        File file = new File(FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (fields.length > 0 && fields[0].equals(id)) {
                        return false; // Duplicate found
                    }
                }
            } catch (IOException e) {
                System.out.println("⚠️ Could not check duplicate IDs: " + e.getMessage());
            }
        }

        return true;
    }

    private boolean isValidBirthdate(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            LocalDate parsedDate = LocalDate.parse(birthdate, formatter);

            // Optional: check that date is not in the future
            if (parsedDate.isAfter(LocalDate.now())) {
                return false;
            }

            // Optional: age should be realistic (not older than 120 years)
            long age = ChronoUnit.YEARS.between(parsedDate, LocalDate.now());
            return age >= 0 && age <= 120;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private int getAgeFromBirthdate(String birthdate) {
        return Period.between(LocalDate.parse(birthdate), LocalDate.now()).getYears();
    }

    /**
     * Adds a person record directly using given parameters, bypassing user input.
     * For testing purposes only.
     */
    public boolean addPersonDirect(int runMode, String id, String firstName, String lastName, String streetNumber,
                                   String streetName, String city, String state, String country, String birthdate) {
        String FILE = "";

        if (runMode == 0) {
            FILE = "persons.txt";
        } else if (runMode == 1) {
            FILE = "persons_test.txt";
        }
        if (!isValidId(FILE, id)) return false;
        if (!firstName.matches("[A-Za-z]+")) return false;
        if (!lastName.matches("[A-Za-z]+")) return false;
        if (!streetNumber.matches("\\d+")) return false;
        if (!streetName.matches("[A-Za-z ]+")) return false;
        if (!city.matches("[A-Za-z ]+")) return false;
        if (!state.equalsIgnoreCase("Victoria")) return false;
        if (!country.matches("[A-Za-z ]+")) return false;
        if (!isValidBirthdate(birthdate)) return false;

        String address = streetNumber + "|" + streetName + "|" + city + "|" + state + "|" + country;
        try (FileWriter writer = new FileWriter(FILE, true)) {
            writer.write(id + "," + firstName + "," + lastName + "," + address + "," + birthdate + ",0,false\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Updates a person's details directly using given parameters (for testing purposes only).
     * Only updates the matching person ID. Applies same rules as interactive function.
     */
    public boolean updatePersonDirect(int runMode, String targetId, String newId, String newFirstName, String newLastName,
                                      String streetNumber, String streetName, String city, String state,
                                      String country, String newBirthdate) {
        String FILE = (runMode == 0) ? "persons.txt" : "persons_test.txt";
        File inputFile = new File(FILE);
        File tempFile = new File("temp_" + FILE);

        boolean found = false;

        DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 6) {
                    writer.write(line + "\n");
                    continue;
                }

                if (!fields[0].equals(targetId)) {
                    writer.write(line + "\n");
                    continue;
                }

                found = true;
                String originalId = fields[0];
                String originalFirstName = fields[1];
                String originalLastName = fields[2];
                String originalAddress = fields[3];
                String originalBirthdate = fields[4];
                String isSuspended = fields[5];

                // === Conditions ===
                if (!newId.equals(originalId)) {
                    char firstDigit = originalId.charAt(0);
                    if (Character.isDigit(firstDigit) && (firstDigit - '0') % 2 == 0) return false;
                    if (!isValidId(FILE, newId)) return false;
                }

                int age = getAgeFromBirthdate(originalBirthdate);

                String newAddress = streetNumber + "|" + streetName + "|" + city + "|" + state + "|" + country;
                if (age < 18 && !newAddress.equals(originalAddress)) return false;

                boolean otherFieldsChanged = !newId.equals(originalId) ||
                        !newFirstName.equals(originalFirstName) ||
                        !newLastName.equals(originalLastName) ||
                        !newAddress.equals(originalAddress);

                if (!newBirthdate.equals(originalBirthdate) && otherFieldsChanged) return false;

                writer.write(newId + "," + newFirstName + "," + newLastName + "," +
                        newAddress + "," + newBirthdate + "," + isSuspended + "\n");
            }
        } catch (IOException e) {
            return false;
        }

        if (!found) return false;
        if (!inputFile.delete()) return false;
        return tempFile.renameTo(inputFile);
    }

    public boolean addDemeritsDirect(int runMode, String personId, String offenseDateStr, int pointsToAdd) {
        String personFile = runMode == 0 ? "persons.txt" : "persons_test.txt";
        String offenseFile = runMode == 0 ? "offenses.txt" : "offenses_test.txt";

        File inputFile = new File(personFile);
        File offenseData = new File(offenseFile);
        String matchedLine = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 6 && fields[0].equals(personId)) {
                    matchedLine = line;
                    break;
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (matchedLine == null) return false;

        String[] fields = matchedLine.split(",");
        String birthdate = fields[4];
        boolean suspended = Boolean.parseBoolean(fields[5]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birth = LocalDate.parse(birthdate, formatter);
        LocalDate offenseDate;

        try {
            offenseDate = LocalDate.parse(offenseDateStr, formatter);
            if (offenseDate.isAfter(LocalDate.now()) || offenseDate.isBefore(birth)) return false;
        } catch (DateTimeParseException e) {
            return false;
        }

        if (pointsToAdd < 1 || pointsToAdd > 6) return false;

        int recentTotal = pointsToAdd;
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);

        try (BufferedReader offenseReader = new BufferedReader(new FileReader(offenseData))) {
            recentTotal = getRecentTotal(personId, formatter, recentTotal, twoYearsAgo, offenseReader);
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            return false;
        }

        try (FileWriter offenseWriter = new FileWriter(offenseFile, true)) {
            offenseWriter.write(personId + "," + offenseDateStr + "," + pointsToAdd + "\n");
        } catch (IOException e) {
            return false;
        }

        int age = Period.between(birth, LocalDate.now()).getYears();
        if ((age < 21 && recentTotal > 6) || (age >= 21 && recentTotal > 12)) {
            suspended = true;
        }

        File tempFile = new File("temp_" + personFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] rec = line.split(",");
                if (rec.length == 6 && rec[0].equals(personId)) {
                    rec[5] = String.valueOf(suspended);
                    writer.write(String.join(",", rec) + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException e) {
            return false;
        }

        if (!inputFile.delete()) return false;

        return tempFile.renameTo(inputFile);
    }
}
