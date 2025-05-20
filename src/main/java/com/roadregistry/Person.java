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

        System.out.println("Enter person ID (10 characters, special format required):");
        this.id = scanner.nextLine();

        System.out.println("Enter first name:");
        this.firstName = scanner.nextLine();

        System.out.println("Enter last name:");
        this.lastName = scanner.nextLine();

        System.out.println("Enter address (StreetNo|Street|City|State|Country):");
        this.address = scanner.nextLine();

        System.out.println("Enter birthdate (DD-MM-YYYY):");
        this.birthdate = scanner.nextLine();

        if (!isValidId(id)) {
            System.out.println("❌ Invalid ID format.");
            return false;
        }

        if (!isValidAddress(address)) {
            System.out.println("❌ Invalid address format.");
            return false;
        }

        if (!isValidBirthdate(birthdate)) {
            System.out.println("❌ Invalid birthdate format.");
            return false;
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
        if (!id.matches("^[2-9][0-9].{6}[A-Z]{2}$")) return false;

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
                    writer.write(currentId + "," + currentName + "," + currentAge + "," + updatedPoints + "\n");
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
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getDemeritPoints() {
        return demeritPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDemeritPoints(int demeritPoints) {
        this.demeritPoints = demeritPoints;
    }
}
