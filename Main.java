package com.mycompany.petspahotel;

import java.util.*;

public class Main {
    private static DatabaseManager dbManager;
    private static Scanner scanner;
    private static User currentUser;
    private static List<Pet> userPets;
    
    public static void main(String[] args) {
        dbManager = DatabaseManager.getInstance();
        scanner = new Scanner(System.in);
        userPets = new ArrayList<>();
        
        System.out.println("=== PET SHOP MANAGEMENT SYSTEM ===");
        System.out.println("Welcome to Pet Shop, Pet Hotel, and Pet Spa");
        System.out.println("Version 1.0");
        
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Sign Up");
            System.out.println("2. Sign In");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    signUp();
                    break;
                case 2:
                    signIn();
                    break;
                case 3:
                    System.out.println("Thank you for using Pet Shop Management System!");
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    static void signUp() {
        System.out.println("\n=== CREATE ACCOUNT ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }
        
        if (password.length() < 4) {
            System.out.println("Password must be at least 4 characters long.");
            return;
        }
        
        boolean success = dbManager.createUser(username, password);
        if (success) {
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Failed to create account. Username may already exist.");
        }
    }
    
    static void signIn() {
        System.out.println("\n=== SIGN IN ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        currentUser = dbManager.authenticateUser(username, password);
        
        if (currentUser != null) {
            System.out.println("Login successful!");
            System.out.println("Welcome back, " + username + "!");
            
            // Load user's pets
            userPets = dbManager.getUserPets(currentUser.getId());
            if (!userPets.isEmpty()) {
                System.out.println("You have " + userPets.size() + " pet(s) registered.");
            }
            
            mainMenu();
        } else {
            System.out.println("Invalid username or password.");
        }
    }
    
    static void mainMenu() {
        while (true) {
            System.out.println("\n=== MAIN DASHBOARD ===");
            System.out.println("1. Manage Pets");
            System.out.println("2. View/Book Pet Hotel");
            System.out.println("3. View/Book Spa Services");
            System.out.println("4. View My Bookings");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    managePets();
                    break;
                case 2:
                    bookHotel();
                    break;
                case 3:
                    bookSpa();
                    break;
                case 4:
                    viewBookings();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    currentUser = null;
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    static void managePets() {
        while (true) {
            System.out.println("\n=== PET MANAGEMENT ===");
            System.out.println("1. Add New Pet");
            System.out.println("2. View My Pets");
            System.out.println("3. Update Pet Health Condition");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    addPet();
                    break;
                case 2:
                    viewPets();
                    break;
                case 3:
                    updatePetHealth();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    static void addPet() {
        System.out.println("\n=== ADD NEW PET ===");
        System.out.print("Pet Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Breed: ");
        String breed = scanner.nextLine();
        
        System.out.print("Age (years): ");
        int age = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Health Condition (e.g., Healthy, Allergies, Diabetic): ");
        String health = scanner.nextLine();
        
        Pet pet = new Pet(name, breed, age, health);
        
        System.out.print("Any special notes? (optional): ");
        String specialNotes = scanner.nextLine();
        pet.setSpecialNotes(specialNotes);
        
        boolean success = dbManager.savePet(currentUser.getId(), pet);
        if (success) {
            userPets = dbManager.getUserPets(currentUser.getId());
            System.out.println("Pet added successfully!");
            pet.displayPetInfo();
        } else {
            System.out.println("Failed to add pet.");
        }
    }
    
    static void viewPets() {
        if (userPets.isEmpty()) {
            System.out.println("\nNo pets registered yet. Please add a pet first.");
            return;
        }
        
        System.out.println("\n=== MY PETS ===");
        for (int i = 0; i < userPets.size(); i++) {
            Pet pet = userPets.get(i);
            System.out.println((i + 1) + ". " + pet.getName() + " - " + pet.getBreed() + 
                             " (" + pet.getAge() + " years) - " + pet.getHealthCondition());
        }
        
        System.out.print("\nEnter pet number to view details (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= userPets.size()) {
            userPets.get(choice - 1).displayPetInfo();
        }
    }
    
    static void updatePetHealth() {
        if (userPets.isEmpty()) {
            System.out.println("\nNo pets registered yet.");
            return;
        }
        
        System.out.println("\n=== UPDATE PET HEALTH ===");
        for (int i = 0; i < userPets.size(); i++) {
            Pet pet = userPets.get(i);
            System.out.println((i + 1) + ". " + pet.getName() + " - Current: " + pet.getHealthCondition());
        }
        
        System.out.print("Select pet to update (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= userPets.size()) {
            Pet pet = userPets.get(choice - 1);
            System.out.print("Enter new health condition for " + pet.getName() + ": ");
            String newHealth = scanner.nextLine();
            
            boolean success = dbManager.updatePetHealthCondition(pet.getId(), newHealth);
            if (success) {
                pet.setHealthCondition(newHealth);
                System.out.println("Health condition updated successfully!");
            } else {
                System.out.println("Failed to update health condition.");
            }
        }
    }
    
    static void bookHotel() {
        if (userPets.isEmpty()) {
            System.out.println("\nPlease add a pet first before booking hotel services.");
            return;
        }
        
        System.out.println("\n=== PET HOTEL BOOKING ===");
        System.out.println("Available Rooms: Standard, Deluxe, VIP Suite");
        System.out.println("Check-in: 2:00 PM");
        System.out.println("Check-out: 12:00 PM");
        
        System.out.println("\nSelect pet for hotel booking:");
        for (int i = 0; i < userPets.size(); i++) {
            Pet pet = userPets.get(i);
            System.out.println((i + 1) + ". " + pet.getName() + " - " + pet.getBreed());
        }
        System.out.print("Choose pet (0 to cancel): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= userPets.size()) {
            Pet selectedPet = userPets.get(choice - 1);
            
            System.out.print("Enter number of nights: ");
            int nights = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Select room type (Standard/Deluxe/VIP): ");
            String roomType = scanner.nextLine();
            
            double totalCost = 0;
            if (roomType.equalsIgnoreCase("Standard")) {
                totalCost = nights * 1499;
            } else if (roomType.equalsIgnoreCase("Deluxe")) {
                totalCost = nights * 2999;
            } else if (roomType.equalsIgnoreCase("VIP")) {
                totalCost = nights * 4999;
            } else {
                System.out.println("Invalid room type.");
                return;
            }
            
            String bookingDetails = "Hotel - " + nights + " nights, " + roomType + " room (₱ " + totalCost + ")";
            
            boolean success = dbManager.createBooking(selectedPet.getId(), bookingDetails);
            if (success) {
                System.out.println("\n✓ Hotel booking confirmed for " + selectedPet.getName() + "!");
                System.out.println("Booking Details: " + bookingDetails);
                System.out.println("Total cost: ₱ " + totalCost);
            } else {
                System.out.println("Failed to create booking.");
            }
        }
    }
    
    static void bookSpa() {
        if (userPets.isEmpty()) {
            System.out.println("\nPlease add a pet first before booking spa services.");
            return;
        }
        
        System.out.println("\n=== PET SPA SERVICES ===");
        System.out.println("Available Services:");
        System.out.println("1. Basic Grooming - ₱499");
        System.out.println("2. Full Grooming - ₱999");
        System.out.println("3. Massage Therapy - ₱1099");
        System.out.println("4. Dental Cleaning - ₱ 999");
        System.out.println("5. Luxury Spa Package - ₱1499");
        
        System.out.print("\nSelect service (1-5): ");
        int serviceChoice = scanner.nextInt();
        scanner.nextLine();
        
        String serviceName = "";
        double price = 0;
        switch (serviceChoice) {
            case 1:
                serviceName = "Basic Grooming";
                price = 499;
                break;
            case 2:
                serviceName = "Full Grooming";
                price = 999;
                break;
            case 3:
                serviceName = "Massage Therapy";
                price = 1099;
                break;
            case 4:
                serviceName = "Dental Cleaning";
                price = 999;
                break;
            case 5:
                serviceName = "Luxury Spa Package";
                price = 1499;
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        System.out.println("\nSelect pet for spa service:");
        for (int i = 0; i < userPets.size(); i++) {
            Pet pet = userPets.get(i);
            System.out.println((i + 1) + ". " + pet.getName() + " - " + pet.getBreed());
        }
        System.out.print("Choose pet (0 to cancel): ");
        
        int petChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (petChoice > 0 && petChoice <= userPets.size()) {
            Pet selectedPet = userPets.get(petChoice - 1);
            
            String bookingDetails = "Spa - " + serviceName + " ($" + price + ")";
            boolean success = dbManager.createBooking(selectedPet.getId(), bookingDetails);
            
            if (success) {
                System.out.println("\n✓ Spa service booked for " + selectedPet.getName() + "!");
                System.out.println("Service: " + serviceName);
                System.out.println("Price: $" + price);
                System.out.println("Please arrive 10 minutes before your appointment.");
            } else {
                System.out.println("Failed to create booking.");
            }
        }
    }
    
    static void viewBookings() {
        List<Booking> bookings = dbManager.getUserBookings(currentUser.getId());
        
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found.");
            return;
        }
        
        System.out.println("\n=== MY BOOKINGS ===");
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            System.out.println((i + 1) + ". " + booking.getPetName() + " - " + 
                             booking.getBookingType() + " - " + booking.getStatus());
            System.out.println("   Details: " + booking.getBookingType());
            System.out.println("   Date: " + booking.getBookingDate());
        }
        
        System.out.print("\nEnter booking number to cancel (0 to exit): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= bookings.size()) {
            Booking booking = bookings.get(choice - 1);
            if (booking.getStatus().equals("active")) {
                System.out.print("Are you sure you want to cancel this booking? (y/n): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    boolean success = dbManager.cancelBooking(booking.getId());
                    if (success) {
                        System.out.println("✓ Booking cancelled successfully.");
                    } else {
                        System.out.println("Failed to cancel booking.");
                    }
                }
            } else {
                System.out.println("This booking is already cancelled.");
            }
        }
    }
}
