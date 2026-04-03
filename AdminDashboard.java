package PetSpaHotel;

import java.util.*;

public class AdminDashboard {
    private DatabaseManager dbManager;
    private Scanner scanner;
    private User adminUser;
    
    public AdminDashboard(DatabaseManager dbManager, Scanner scanner, User adminUser) {
        this.dbManager = dbManager;
        this.scanner = scanner;
        this.adminUser = adminUser;
    }
    
    public void showAdminMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║        ADMIN DASHBOARD                ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("Welcome, " + adminUser.getUsername() + " (Administrator)");
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Spa Services");
            System.out.println("2. Manage Rooms");
            System.out.println("3. View All Bookings");
            System.out.println("4. View All Users");
            System.out.println("5. View System Statistics");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    manageSpaServices();
                    break;
                case 2:
                    manageRooms();
                    break;
                case 3:
                    viewAllBookings();
                    break;
                case 4:
                    viewAllUsers();
                    break;
                case 5:
                    viewSystemStatistics();
                    break;
                case 6:
                    System.out.println("Logging out from admin dashboard...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void manageSpaServices() {
        while (true) {
            System.out.println("\n=== MANAGE SPA SERVICES ===");
            System.out.println("1. View All Spa Services");
            System.out.println("2. Add New Spa Service");
            System.out.println("3. Update Spa Service");
            System.out.println("4. Delete Spa Service");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllSpaServices();
                    break;
                case 2:
                    addSpaService();
                    break;
                case 3:
                    updateSpaService();
                    break;
                case 4:
                    deleteSpaService();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void viewAllSpaServices() {
        List<SpaService> services = dbManager.getAllSpaServices();
        
        if (services.isEmpty()) {
            System.out.println("\nNo spa services found.");
            return;
        }
        
        System.out.println("\n=== ALL SPA SERVICES ===");
        for (SpaService service : services) {
            System.out.println("\nID: " + service.getId());
            System.out.println("Name: " + service.getName());
            System.out.println("Description: " + service.getDescription());
            System.out.println("Price: ₱" + service.getPrice());
            System.out.println("Duration: " + service.getDuration() + " minutes");
        }
    }
    
    private void addSpaService() {
        System.out.println("\n=== ADD NEW SPA SERVICE ===");
        System.out.print("Service Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Price: ₱");
        double price = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.print("Duration (minutes): ");
        int duration = scanner.nextInt();
        scanner.nextLine();
        
        if (dbManager.addSpaService(name, description, price, duration)) {
            System.out.println("\nSpa service added successfully!");
        } else {
            System.out.println("\nFailed to add spa service.");
        }
    }
    
    private void updateSpaService() {
        List<SpaService> services = dbManager.getAllSpaServices();
        
        if (services.isEmpty()) {
            System.out.println("\nNo spa services to update.");
            return;
        }
        
        viewAllSpaServices();
        
        System.out.print("\nEnter Service ID to update (0 to cancel): ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        if (id == 0) return;
        
        SpaService serviceToUpdate = null;
        for (SpaService service : services) {
            if (service.getId() == id) {
                serviceToUpdate = service;
                break;
            }
        }
        
        if (serviceToUpdate == null) {
            System.out.println("Service not found.");
            return;
        }
        
        System.out.println("\nUpdating Service: " + serviceToUpdate.getName());
        System.out.print("New Name (Enter to keep: " + serviceToUpdate.getName() + "): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = serviceToUpdate.getName();
        
        System.out.print("New Description (Enter to keep: " + serviceToUpdate.getDescription() + "): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = serviceToUpdate.getDescription();
        
        System.out.print("New Price (Enter to keep: ₱" + serviceToUpdate.getPrice() + "): ");
        String priceInput = scanner.nextLine();
        double price = priceInput.isEmpty() ? serviceToUpdate.getPrice() : Double.parseDouble(priceInput);
        
        System.out.print("New Duration (Enter to keep: " + serviceToUpdate.getDuration() + " min): ");
        String durationInput = scanner.nextLine();
        int duration = durationInput.isEmpty() ? serviceToUpdate.getDuration() : Integer.parseInt(durationInput);
        
        System.out.print("Is Active? (y/n): ");
        String activeInput = scanner.nextLine();
        boolean isActive = activeInput.equalsIgnoreCase("y");
        
        if (dbManager.updateSpaService(id, name, description, price, duration, isActive)) {
            System.out.println("\nSpa service updated successfully!");
        } else {
            System.out.println("\nFailed to update spa service.");
        }
    }
    
    private void deleteSpaService() {
        List<SpaService> services = dbManager.getAllSpaServices();
        
        if (services.isEmpty()) {
            System.out.println("\nNo spa services to delete.");
            return;
        }
        
        viewAllSpaServices();
        
        System.out.print("\nEnter Service ID to delete (0 to cancel): ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        if (id == 0) return;
        
        System.out.print("Are you sure you want to delete this service? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (dbManager.deleteSpaService(id)) {
                System.out.println("\nSpa service deleted successfully!");
            } else {
                System.out.println("\nFailed to delete spa service.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private void manageRooms() {
        while (true) {
            System.out.println("\n=== MANAGE ROOMS ===");
            System.out.println("1. View All Rooms");
            System.out.println("2. Add New Room");
            System.out.println("3. Update Room");
            System.out.println("4. Delete Room");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllRooms();
                    break;
                case 2:
                    addRoom();
                    break;
                case 3:
                    updateRoom();
                    break;
                case 4:
                    deleteRoom();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void viewAllRooms() {
        List<Room> rooms = dbManager.getAllRooms();
        
        if (rooms.isEmpty()) {
            System.out.println("\nNo rooms found.");
            return;
        }
        
        System.out.println("\n=== ALL ROOMS ===");
        for (Room room : rooms) {
            System.out.println("\nID: " + room.getId());
            System.out.println("Room Type: " + room.getRoomType());
            System.out.println("Price per Night: ₱" + room.getPricePerNight());
            System.out.println("Status: " + (room.isAvailable() ? "Available" : "Not Available"));
            System.out.println("Description: " + room.getDescription());
        }
    }
    
    private void addRoom() {
        System.out.println("\n=== ADD NEW ROOM ===");
        System.out.print("Room Type: ");
        String roomType = scanner.nextLine();
        
        System.out.print("Price per Night: ₱");
        double pricePerNight = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        if (dbManager.addRoom(roomType, pricePerNight, description)) {
            System.out.println("\nRoom added successfully!");
        } else {
            System.out.println("\nFailed to add room.");
        }
    }
    
    private void updateRoom() {
        List<Room> rooms = dbManager.getAllRooms();
        
        if (rooms.isEmpty()) {
            System.out.println("\nNo rooms to update.");
            return;
        }
        
        viewAllRooms();
        
        System.out.print("\nEnter Room ID to update (0 to cancel): ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        if (id == 0) return;
        
        Room roomToUpdate = null;
        for (Room room : rooms) {
            if (room.getId() == id) {
                roomToUpdate = room;
                break;
            }
        }
        
        if (roomToUpdate == null) {
            System.out.println("Room not found.");
            return;
        }
        
        System.out.println("\nUpdating Room: " + roomToUpdate.getRoomType());
        System.out.print("New Room Type (Enter to keep: " + roomToUpdate.getRoomType() + "): ");
        String roomType = scanner.nextLine();
        if (roomType.isEmpty()) roomType = roomToUpdate.getRoomType();
        
        System.out.print("New Price per Night (Enter to keep: ₱" + roomToUpdate.getPricePerNight() + "): ");
        String priceInput = scanner.nextLine();
        double pricePerNight = priceInput.isEmpty() ? roomToUpdate.getPricePerNight() : Double.parseDouble(priceInput);
        
        System.out.print("Is Available? (y/n): ");
        String availableInput = scanner.nextLine();
        boolean isAvailable = availableInput.equalsIgnoreCase("y");
        
        System.out.print("New Description (Enter to keep: " + roomToUpdate.getDescription() + "): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = roomToUpdate.getDescription();
        
        if (dbManager.updateRoom(id, roomType, pricePerNight, isAvailable, description)) {
            System.out.println("\nRoom updated successfully!");
        } else {
            System.out.println("\nFailed to update room.");
        }
    }
    
    private void deleteRoom() {
        List<Room> rooms = dbManager.getAllRooms();
        
        if (rooms.isEmpty()) {
            System.out.println("\nNo rooms to delete.");
            return;
        }
        
        viewAllRooms();
        
        System.out.print("\nEnter Room ID to delete (0 to cancel): ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        if (id == 0) return;
        
        System.out.print("Are you sure you want to delete this room? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (dbManager.deleteRoom(id)) {
                System.out.println("\nRoom deleted successfully!");
            } else {
                System.out.println("\nFailed to delete room.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private void viewAllBookings() {
        List<Booking> bookings = dbManager.getAllBookings();
        
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found.");
            return;
        }
        
        System.out.println("\n=== ALL BOOKINGS ===");
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
            
            if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
                System.out.println("   Requests: " + booking.getSpecialRequests());
            }
        }
    }
    
    private void viewAllUsers() {
        List<User> users = dbManager.getAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }
        
        System.out.println("\n=== ALL USERS ===");
        for (User user : users) {
            System.out.println("\nID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Role: " + (user.isAdmin() ? "Administrator" : "Pet Owner"));
        }
    }
    
    private void viewSystemStatistics() {
        List<Booking> allBookings = dbManager.getAllBookings();
        List<User> allUsers = dbManager.getAllUsers();
        List<SpaService> allServices = dbManager.getAllSpaServices();
        List<Room> allRooms = dbManager.getAllRooms();
        
        int activeBookings = 0;
        int cancelledBookings = 0;
        double totalRevenue = 0;
        
        for (Booking booking : allBookings) {
            if (booking.getStatus().equals("active")) {
                activeBookings++;
                totalRevenue += booking.getServicePrice();
            } else if (booking.getStatus().equals("cancelled")) {
                cancelledBookings++;
            }
        }
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        SYSTEM STATISTICS              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\n=== USERS ===");
        System.out.println("Total Users: " + allUsers.size());
        
        System.out.println("\n=== BOOKINGS ===");
        System.out.println("Total Bookings: " + allBookings.size());
        System.out.println("Active Bookings: " + activeBookings);
        System.out.println("Cancelled Bookings: " + cancelledBookings);
        System.out.println("Total Revenue: ₱" + totalRevenue);
        
        System.out.println("\n=== SERVICES ===");
        System.out.println("Available Spa Services: " + allServices.size());
        System.out.println("Available Room Types: " + allRooms.size());
    }
}