package com.roadregistry;

import java.io.*;

/**
 * Represents a person in the RoadRegistry system.
 * Supports adding new person records to a text file.
 */
public class Person {
    private String id;
    private String name;
    private int age;

    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    /**
     * Appends a person's record to persons.txt file.
     *
     * @return true if saved successfully, false otherwise.
     */
    public boolean addPerson() {
        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(id + "," + name + "," + age + "\n");
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
                if (fields.length != 3) continue;

                String currentId = fields[0];

                if (currentId.equals(this.id)) {
                    // Replace the line with updated details
                    writer.write(id + "," + name + "," + age + "\n");
                    updated = true;
                } else {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException e) {
            System.out.println("Error updating person: " + e.getMessage());
            return false;
        }
        return updated;
    }

    // Getters and setters (used for tests- Matthew)
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}
