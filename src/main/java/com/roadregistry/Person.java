package com.roadregistry;

import java.io.*;
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
     *
     * @return true if saved successfully, false otherwise.
     */
    public boolean addPerson() {
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

        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(id + "," + firstName + "," + lastName + "," + address + "," + birthdate + "," + demeritPoints + "," + isSuspended + "\n");
            System.out.println("✅ Person added successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
            return false;
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

    private boolean isValidAddress(String address) {
        String[] parts = address.split("\\|");
        return parts.length == 5 && parts[3].trim().equalsIgnoreCase("Victoria");
    }

    private boolean isValidBirthdate(String birthdate) {
        return birthdate.matches("^\\d{2}-\\d{2}-\\d{4}$");
    }

    /**
     * Updates the personal details (name and age) of a person by ID.
     * Reads from "persons.txt", updates the matching line, and writes it back.
     *
     * @return true if updated successfully, false if ID not found or error occurred.
     */
    public boolean updatePersonalDetails() {
        File inputFile = new File("persons.txt");
        File tempFile = new File("persons_temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 7) continue;

                String currentId = fields[0];

                if (currentId.equals(this.id)) {
                    writer.write(id + "," + firstName + "," + lastName + "," + address + "," + birthdate + "," + demeritPoints + "," + isSuspended + "\n");
                    updated = true;
                } else {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException e) {
            System.out.println("Error updating person: " + e.getMessage());
            return false;
        }

        if (updated) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("Could not finalize file update.");
                return false;
            }
        } else {
            tempFile.delete();
        }

        return updated;
    }

    /**
     * Adds demerit points to a person's record identified by ID.
     * Reads from "persons.txt", updates the matching line, and writes it back.
     *
     * @param pointsToAdd Number of points to add
     * @return true if update was successful, false if ID not found or error occurred.
     */
    public boolean addDemeritPoints(int pointsToAdd) {
        File inputFile = new File("persons.txt");
        File tempFile = new File("persons_temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) continue;

                String currentId = fields[0];
                String currentName = fields[1];
                int currentAge = Integer.parseInt(fields[2]);
                int currentPoints = Integer.parseInt(fields[3]);

                if (currentId.equals(this.id)) {
                    int updatedPoints = currentPoints + pointsToAdd;
                    writer.write(fields[0] + "," + fields[1] + "," + fields[2] + "," + fields[3] + "," + fields[4] + "," + updatedPoints + "," + fields[6] + "\n");
                    updated = true;
                } else {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error updating demerit points: " + e.getMessage());
            return false;
        }

        if (updated) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("Could not finalize file update.");
                return false;
            }
        } else {
            tempFile.delete();
        }

        return updated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        String[] parts = birthdate.split("-");
        int birthYear = Integer.parseInt(parts[2]);
        return 2025 - birthYear; // crude calculation
    }

    public int getDemeritPoints() {
        return demeritPoints;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDemeritPoints(int demeritPoints) {
        this.demeritPoints = demeritPoints;
    }
}
