package PetSpaHotel;

import java.util.*;

public class Main {
    private static DatabaseManager dbManager;
    private static Scanner scanner;
    private static User currentUser;
    private static List<Pet> userPets;
    private static List<SpaService> spaServices;
    private static CancelSpaService cancelSpaService;
    private static CancelHotelBooking cancelHotelBooking;
    
    public static void main(String[] args) {
        dbManager = DatabaseManager.getInstance();
        scanner = new Scanner(System.in);
        userPets = new ArrayList<>();
        spaServices = new ArrayList<>();
        
        System.out.println("=== PET SHOP MANAGEMENT SYSTEM ===");
        System.out.println("Welcome to Pet Shop, Pet Hotel, and Pet Spa");
        
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
            System.out.println("Passwords do not match.");
            return;
        }
        
        if (password.length() < 4) {
            System.out.println("Password must be at least 4 characters.");
            return;
        }
        
        if (dbManager.createUser(username, password)) {
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
            System.out.println("Login successful! Welcome back, " + username + "!");
            
            if (currentUser.isAdmin()) {
                AdminDashboard adminDashboard = new AdminDashboard(dbManager, scanner, currentUser);
                adminDashboard.showAdminMenu();
            } else {
                userPets = dbManager.getUserPets(currentUser.getId());
                if (!userPets.isEmpty()) {
                    System.out.println("You have " + userPets.size() + " pet(s) registered.");
                }
                
                spaServices = dbManager.getAvailableSpaServices();
                cancelSpaService = new CancelSpaService(dbManager, scanner, currentUser);
                cancelHotelBooking = new CancelHotelBooking(dbManager, scanner, currentUser);
                
                userMenu();
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }
    
    static void userMenu() {
        while (true) {
            System.out.println("\n=== USER DASHBOARD ===");
            System.out.println("1. Manage Pets");
            System.out.println("2. Book Pet Hotel");
            System.out.println("3. Book Spa Services");
            System.out.println("4. View My Bookings");
            System.out.println("5. Cancel Spa Services");
            System.out.println("6. Cancel Hotel Booking");
            System.out.println("7. Delete Booking");
            System.out.println("8. Logout");
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
                    if (cancelSpaService.hasActiveSpaBookings()) {
                        cancelSpaService.showCancelSpaMenu();
                    } else {
                        System.out.println("\nNo active spa bookings to cancel.");
                    }
                    break;
                case 6:
                    if (cancelHotelBooking.hasActiveHotelBookings()) {
                        cancelHotelBooking.showCancelHotelMenu();
                    } else {
                        System.out.println("\nNo active hotel bookings to cancel.");
                    }
                    break;
                case 7:
                    deleteBooking();
                    break;
                case 8:
                    System.out.println("Logging out...");
                    currentUser = null;
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    static void managePets() {
        while (true) {
            System.out.println("\n=== PET MANAGEMENT ===");
            System.out.println("1. Add New Pet");
            System.out.println("2. View My Pets");
            System.out.println("3. Update Pet Health");
            System.out.println("4. Remove Pet");
            System.out.println("5. Back");
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
                    removePet();
                    break;
                case 5:
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
        
        System.out.print("Health Condition: ");
        String health = scanner.nextLine();
        
        Pet pet = new Pet(name, breed, age, health);
        
        System.out.print("Special Notes (optional): ");
        String specialNotes = scanner.nextLine();
        pet.setSpecialNotes(specialNotes);
        
        if (dbManager.savePet(currentUser.getId(), pet)) {
            userPets = dbManager.getUserPets(currentUser.getId());
            System.out.println("Pet added successfully!");
            pet.displayPetInfo();
        } else {
            System.out.println("Failed to add pet.");
        }
    }
    
    static void viewPets() {
        if (userPets.isEmpty()) {
            System.out.println("\nNo pets registered.");
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
            System.out.println("\nNo pets registered.");
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
            System.out.print("New health condition: ");
            String newHealth = scanner.nextLine();
            
            if (dbManager.updatePetHealthCondition(pet.getId(), newHealth)) {
                pet.setHealthCondition(newHealth);
                System.out.println("Health condition updated!");
            } else {
                System.out.println("Update failed.");
            }
        }
    }
    
    static void removePet() {
        if (userPets.isEmpty()) {
            System.out.println("\nNo pets registered to remove.");
            return;
        }
        
        System.out.println("\n=== REMOVE PET ===");
        for (int i = 0; i < userPets.size(); i++) {
            Pet pet = userPets.get(i);
            System.out.println((i + 1) + ". " + pet.getName() + " - " + pet.getBreed() + 
                             " (" + pet.getAge() + " years) - " + pet.getHealthCondition());
            
            List<Booking> bookings = dbManager.getUserBookings(currentUser.getId());
            boolean hasActiveBookings = false;
            for (Booking booking : bookings) {
                if (booking.getPetId() == pet.getId() && booking.getStatus().equals("active")) {
                    hasActiveBookings = true;
                    break;
                }
            }
            
            if (hasActiveBookings) {
                System.out.println("   WARNING: Has active bookings - cannot remove");
            }
        }
        
        System.out.print("\nSelect pet to remove (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= userPets.size()) {
            Pet petToRemove = userPets.get(choice - 1);
            
            List<Booking> bookings = dbManager.getUserBookings(currentUser.getId());
            boolean hasActiveBookings = false;
            for (Booking booking : bookings) {
                if (booking.getPetId() == petToRemove.getId() && booking.getStatus().equals("active")) {
                    hasActiveBookings = true;
                    break;
                }
            }
            
            if (hasActiveBookings) {
                System.out.println("\nCannot remove " + petToRemove.getName() + " because it has active bookings.");
                System.out.println("Please cancel all bookings for this pet first.");
                return;
            }
            
            System.out.println("\nPet Details:");
            petToRemove.displayPetInfo();
            System.out.print("\nAre you sure you want to remove this pet? (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                if (dbManager.deletePet(petToRemove.getId())) {
                    userPets = dbManager.getUserPets(currentUser.getId());
                    System.out.println("\nPet removed successfully!");
                } else {
                    System.out.println("\nFailed to remove pet.");
                }
            } else {
                System.out.println("Removal cancelled.");
            }
        }
    }
    
    static void bookHotel() {
        List<Room> rooms = dbManager.getAllRooms();
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        
        if (availableRooms.isEmpty()) {
            System.out.println("\nNo rooms available at the moment.");
            return;
        }
        
        if (userPets.isEmpty()) {
            System.out.println("\nPlease add a pet first.");
            return;
        }
        
        System.out.println("\n=== PET HOTEL BOOKING ===");
        System.out.println("\nAvailable Rooms:");
        for (int i = 0; i < availableRooms.size(); i++) {
            Room room = availableRooms.get(i);
            System.out.println((i + 1) + ". " + room.getRoomType() + " - ₱" + room.getPricePerNight() + "/night");
            System.out.println("   " + room.getDescription());
        }
        
        System.out.print("\nSelect room type (1-" + availableRooms.size() + "): ");
        int roomChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (roomChoice < 1 || roomChoice > availableRooms.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Room selectedRoom = availableRooms.get(roomChoice - 1);
        
        System.out.println("\nSelect pet:");
        for (int i = 0; i < userPets.size(); i++) {
            System.out.println((i + 1) + ". " + userPets.get(i).getName());
        }
        System.out.print("Choose pet (0 to cancel): ");
        
        int petChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (petChoice > 0 && petChoice <= userPets.size()) {
            Pet selectedPet = userPets.get(petChoice - 1);
            
            System.out.print("Number of nights: ");
            int nights = scanner.nextInt();
            scanner.nextLine();
            
            double totalCost = nights * selectedRoom.getPricePerNight();
            
            if (dbManager.createHotelBooking(selectedPet.getId(), nights, selectedRoom.getRoomType(), totalCost)) {
                System.out.println("\nHotel booking confirmed for " + selectedPet.getName() + "!");
                System.out.println("Room: " + selectedRoom.getRoomType());
                System.out.println("Nights: " + nights);
                System.out.println("Total: ₱" + totalCost);
            } else {
                System.out.println("Booking failed.");
            }
        }
    }
    
    static void bookSpa() {
        if (userPets.isEmpty()) {
            System.out.println("\nPlease add a pet first.");
            return;
        }
        
        if (spaServices.isEmpty()) {
            System.out.println("No spa services available.");
            return;
        }
        
        System.out.println("\n=== PET SPA SERVICES ===");
        for (int i = 0; i < spaServices.size(); i++) {
            SpaService service = spaServices.get(i);
            System.out.println((i + 1) + ". " + service.getName() + " - ₱" + service.getPrice() + 
                             " (" + service.getDuration() + " min)");
            System.out.println("   " + service.getDescription());
        }
        
        System.out.print("\nSelect service (1-" + spaServices.size() + "): ");
        int serviceChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (serviceChoice < 1 || serviceChoice > spaServices.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        SpaService selectedService = spaServices.get(serviceChoice - 1);
        
        System.out.println("\nSelect pet:");
        for (int i = 0; i < userPets.size(); i++) {
            Pet pet = userPets.get(i);
            System.out.println((i + 1) + ". " + pet.getName() + " - " + pet.getBreed());
            if (pet.hasSpecialNeeds()) {
                System.out.println("   Note: " + pet.getHealthCondition());
            }
        }
        System.out.print("Choose pet (0 to cancel): ");
        
        int petChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (petChoice < 1 || petChoice > userPets.size()) {
            System.out.println("Cancelled.");
            return;
        }
        
        Pet selectedPet = userPets.get(petChoice - 1);
        
        if (selectedPet.hasSpecialNeeds()) {
            System.out.print("\nYour pet has health condition. Proceed? (y/n): ");
            String proceed = scanner.nextLine();
            if (!proceed.equalsIgnoreCase("y")) {
                System.out.println("Cancelled.");
                return;
            }
        }
        
        System.out.println("\nAppointment Details:");
        System.out.print("Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.nextLine();
        
        if (!appointmentDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format.");
            return;
        }
        
        System.out.print("Time (HH:MM): ");
        String appointmentTime = scanner.nextLine();
        
        if (!appointmentTime.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
            System.out.println("Invalid time format.");
            return;
        }
        
        System.out.print("Special requests (optional): ");
        String specialRequests = scanner.nextLine();
        
        System.out.println("\n=== CONFIRM BOOKING ===");
        System.out.println("Pet: " + selectedPet.getName());
        System.out.println("Service: " + selectedService.getName());
        System.out.println("Date: " + appointmentDate + " at " + appointmentTime);
        System.out.println("Price: ₱" + selectedService.getPrice());
        
        System.out.print("\nConfirm booking? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }
        
        if (dbManager.createSpaBooking(selectedPet.getId(), selectedService.getId(),
                appointmentDate, appointmentTime, specialRequests)) {
            System.out.println("\nSPA BOOKING CONFIRMED!");
            System.out.println("Pet: " + selectedPet.getName());
            System.out.println("Service: " + selectedService.getName());
            System.out.println("Appointment: " + appointmentDate + " at " + appointmentTime);
            System.out.println("Price: ₱" + selectedService.getPrice());
        } else {
            System.out.println("Booking failed.");
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
            System.out.println("\n[" + (i + 1) + "] " + booking.getPetName());
            System.out.println("   Type: " + booking.getBookingType());
            System.out.println("   Status: " + booking.getStatus());
            System.out.println("   Service: " + booking.getServiceName());
            System.out.println("   Price: ₱" + booking.getServicePrice());
            
            if (booking.getAppointmentDate() != null) {
                System.out.println("   Appointment: " + booking.getAppointmentDate() + " at " + booking.getAppointmentTime());
            }
            
            if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
                System.out.println("   Requests: " + booking.getSpecialRequests());
            }
        }
    }
    
    static void deleteBooking() {
        List<Booking> bookings = dbManager.getUserBookings(currentUser.getId());
        
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found to delete.");
            return;
        }
        
        System.out.println("\n=== DELETE BOOKING ===");
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            System.out.println("\n[" + (i + 1) + "] Booking ID: " + booking.getId());
            System.out.println("   Pet: " + booking.getPetName());
            System.out.println("   Type: " + booking.getBookingType());
            System.out.println("   Status: " + booking.getStatus());
            System.out.println("   Service: " + booking.getServiceName());
            System.out.println("   Price: ₱" + booking.getServicePrice());
            
            if (booking.getAppointmentDate() != null) {
                System.out.println("   Appointment: " + booking.getAppointmentDate() + " at " + booking.getAppointmentTime());
            }
        }
        
        System.out.print("\nEnter booking number to delete (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= bookings.size()) {
            Booking booking = bookings.get(choice - 1);
            
            System.out.println("\nBooking Details:");
            booking.displayBookingInfo();
            System.out.print("\nAre you sure you want to PERMANENTLY DELETE this booking? (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                if (dbManager.deleteBooking(booking.getId())) {
                    System.out.println("\nBooking permanently deleted!");
                } else {
                    System.out.println("\nFailed to delete booking.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        }
    }
}