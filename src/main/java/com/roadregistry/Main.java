package com.roadregistry;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Person person = new Person("", "", "", "", "");  // temporary placeholder

        while (true) {
            System.out.println("\n========= RoadRegistry Main Menu =========");
            System.out.println("1. Add Person");
            System.out.println("2. Update Personal Details");
            System.out.println("3. Add Demerit Points");
            System.out.println("4. Exit");
            System.out.print("Choose an option (1-4): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    person = new Person("", "", "", "", "");  // re-initialize for clean input
                    person.addPerson();
                    break;
                case "2":
                    // person.updatePersonalDetails(); // we’ll build this next
                    person.updatePersonalDetails();
                    break;
                case "3":
                    // person.addDemeritPoints(); // we’ll build this next
                    System.out.println("🚧 Function not implemented yet.");
                    break;
                case "4":
                    System.out.println("Exiting... 🚪");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
}
