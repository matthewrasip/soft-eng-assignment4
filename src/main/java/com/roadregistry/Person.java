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
public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private int demeritPoints;
    private boolean isSuspended;

    public Person(String id, String firstName, String lastName, String address, String birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.demeritPoints = 0; //default value
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
            if (isValidId(id)) break;
            System.out.println("❌ Invalid ID format.");
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

        // Save to file
        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(id + "," + firstName + "," + lastName + "," + address + "," + birthdate + "," + demeritPoints + "," + isSuspended + "\n");
            System.out.println("✅ Person added successfully.");
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Updates the personal details (name and age) of a person by ID.
     * Reads from "persons.txt", updates the matching line, and writes it back.
     */
    public boolean updatePersonalDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the person you want to update: ");
        String targetId = scanner.nextLine().trim();

        File inputFile = new File("persons.txt");
        File tempFile = new File("persons_temp.txt");

        boolean found = false;
        boolean updated = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 7) {
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
                String demeritPoints = fields[5];
                String isSuspended = fields[6];

                String newId = originalId;
                String newFirstName = originalFirstName;
                String newLastName = originalLastName;
                String newAddress = originalAddress;
                String newBirthdate = originalBirthdate;

                // === Calculate age ===
                LocalDate birth = LocalDate.parse(originalBirthdate, formatter);
                int age = getAgeFromBirthdate(String.valueOf(birth));

                boolean hasOtherChanges = false;
                boolean allowBirthdateChange = true;

                // === ID Update (check even digit restriction) ===
                char firstDigit = originalId.charAt(0);
                String input;
                if (Character.isDigit(firstDigit) && (firstDigit - '0') % 2 == 0) {
                    System.out.println("❌ ID cannot be changed. First digit is even.");
                } else {
                    System.out.print("Enter new ID (or press Enter to keep): ");
                    input = scanner.nextLine().trim();

                    if (!input.isEmpty()) {
                        if (isValidId(input)) {
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
                    if (!input.isEmpty() && input.equalsIgnoreCase("Victoria")) {
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
                        newBirthdate + "," + demeritPoints + "," + isSuspended + "\n");
                updated = true;
            }

            if (!found) {
                System.out.println("❌ Person not found.");
                return false;
            }

        } catch (IOException | DateTimeParseException e) {
            System.out.println("❌ Error updating person: " + e.getMessage());
            return false;
        }

        if (updated) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("❌ Could not finalize update.");
                return false;
            }
            System.out.println("✅ Update successful.");
            return true;
        } else {
            tempFile.delete();
            return false;
        }
    }

    /**
     * Adds demerit points to a person's record identified by ID.
     * Reads from "persons.txt", updates the matching line, and writes it back.
     */
    public void addDemeritPoints() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter person ID: ");
        String personId = scanner.nextLine().trim();

        System.out.print("Enter offense date (DD-MM-YYYY): ");
        String offenseDateStr = scanner.nextLine().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate offenseDate;
        try {
            offenseDate = LocalDate.parse(offenseDateStr, formatter);
            if (offenseDate.isAfter(LocalDate.now())) {
                System.out.println("❌ Offense date cannot be in the future.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format.");
            return;
        }

        File inputFile = new File("persons.txt");
        File tempFile = new File("persons_temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 7) {
                    writer.write(line + "\n");
                    continue;
                }

                if (!fields[0].equals(personId)) {
                    writer.write(line + "\n");
                    continue;
                }

                // Person found
                String birthdate = fields[4];
                int currentPoints = Integer.parseInt(fields[5]);
                boolean suspended = Boolean.parseBoolean(fields[6]);

                LocalDate birth = LocalDate.parse(birthdate, formatter);

                if (offenseDate.isBefore(birth)) {
                    System.out.println("❌ Offense date cannot be before person's birthdate.");
                    return;
                }

                // Age calculated as of today (not offense date)
                long age = ChronoUnit.YEARS.between(birth, LocalDate.now());

                // Only ask for points after confirming all date-related conditions
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

                int updatedPoints = currentPoints + pointsToAdd;

                // Suspension rules
                if (age < 21 && updatedPoints > 6) suspended = true;
                if (age >= 21 && updatedPoints > 12) suspended = true;

                fields[5] = String.valueOf(updatedPoints);
                fields[6] = String.valueOf(suspended);

                writer.write(String.join(",", fields) + "\n");
                updated = true;
            }

        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return;
        }

        if (updated) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("❌ Could not finalize file update.");
                return;
            }
            System.out.println("✅ Demerit points added successfully.");
        } else {
            tempFile.delete();
            System.out.println("❌ Person ID not found.");
        }
    }

    private boolean isValidId(String id) {
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
        return specialCount >= 2;
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
            if (age < 0 || age > 120) {
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    private int getAgeFromBirthdate(String birthdate) {
        return Period.between(LocalDate.parse(birthdate), LocalDate.now()).getYears();
    }

    public int getDemeritPoints() {
        return demeritPoints;
    }

    public void setDemeritPoints(int demeritPoints) {
        this.demeritPoints = demeritPoints;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
