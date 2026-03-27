import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<User> users = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Sign Up");
            System.out.println("2. Sign In");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                signUp();
            } else if (choice == 2) {
                signIn();
            } else {
                System.out.println("Exiting...");
                break;
            }
        }
    }

    static void signUp() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        users.add(new User(username, password));
        System.out.println("Account created successfully!");
    }

    static void signIn() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                System.out.println("Login successful!");
                System.out.println("Welcome, " + username);

                petFlow(); 
                return;
            }
        }

        System.out.println("Invalid username or password.");
    }

   
    static void petFlow() {
        System.out.println("\n=== PET CHARACTERISTICS ===");

        System.out.print("Pet Name: ");
        String name = scanner.nextLine();

        System.out.print("Breed: ");
        String breed = scanner.nextLine();

        System.out.print("Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Health Condition: ");
        String health = scanner.nextLine();

        Pet pet = new Pet(name, breed, age, health);

        System.out.println("\nPet Saved!");
        System.out.println("Pet: " + pet.name + " | " + pet.breed);

        serviceMenu(); // go to next step
    }

    static void serviceMenu() {
        while (true) {
            System.out.println("\n=== SERVICES ===");
            System.out.println("1. Book Pet Room");
            System.out.println("2. Book Spa Service");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.println("Pet Room booked! 🏨");
            } else if (choice == 2) {
                System.out.println("Spa service booked! 🛁");
            } else {
                System.out.println("Logged out.");
                break;
            }
        }
    }
}