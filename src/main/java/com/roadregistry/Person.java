package com.roadregistry;

import java.io.FileWriter;
import java.io.IOException;

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
        // Implementation will go here
        return false;
    }

    // Getters and setters (used for tests- Matthew)
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}
