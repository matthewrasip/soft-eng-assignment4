package com.roadregistry;

/**
 * Entry point to manually test the Person class methods.
 */
public class Main {
    public static void main(String[] args) {

        // Create a new person
        Person person = new Person("P001", "John Doe", 35);
        person.setDemeritPoints(2);

        // Add person to the file
        if (person.addPerson()) {
            System.out.println("Person added successfully.");
        } else {
            System.out.println("Failed to add person.");
        }

        // Update personal details
        person.setName("Jonathan Doe");
        person.setAge(36);

        if (person.updatePersonalDetails()) {
            System.out.println("Personal details updated successfully.");
        } else {
            System.out.println("Failed to update personal details.");
        }

        // Add demerit points
        if (person.addDemeritPoints(3)) {
            System.out.println("Demerit points added successfully.");
        } else {
            System.out.println("Failed to add demerit points.");
        }
    }
}
