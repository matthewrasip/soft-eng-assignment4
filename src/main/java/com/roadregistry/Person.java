package com.roadregistry;

import java.io.*;

/**
 * Represents a person in the RoadRegistry system.
 * Supports adding new person records, updating personal details,
 * and adding demerit points using a text file for storage.
 */
public class Person {
    private String id;
    private String name;
    private int age;
    private int demeritPoints;

    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.demeritPoints = 0;  // Default value
    }

    /**
     * Appends a person's record to persons.txt file.
     *
     * @return true if saved successfully, false otherwise.
     */
    public boolean addPerson() {
        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(id + "," + name + "," + age + "," + demeritPoints + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            return false;
        }
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
                if (fields.length != 4) continue;

                String currentId = fields[0];

                if (currentId.equals(this.id)) {
                    writer.write(id + "," + name + "," + age + "," + demeritPoints + "\n");
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

    // ===============================
    // Getters and Setters
    // ===============================

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
