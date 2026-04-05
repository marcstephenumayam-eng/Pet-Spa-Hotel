package PetSpaHotel;

import java.util.*;

public class CancelHotelBooking {
    private DatabaseManager dbManager;
    private Scanner scanner;
    private User currentUser;
    private List<Booking> activeHotelBookings;
    
    private static final double HOTEL_CANCEL_FEE = 0.10;
    
    public CancelHotelBooking(DatabaseManager dbManager, Scanner scanner, User currentUser) {
        this.dbManager = dbManager;
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.activeHotelBookings = new ArrayList<>();
        loadActiveHotelBookings();
    }
    
    private void loadActiveHotelBookings() {
        List<Booking> allBookings = dbManager.getUserBookings(currentUser.getId());
        activeHotelBookings.clear();
        
        for (Booking booking : allBookings) {
            if (booking.getBookingType().equalsIgnoreCase("Hotel") && 
                booking.getStatus().equalsIgnoreCase("active")) {
                activeHotelBookings.add(booking);
            }
        }
    }
    
    public void showCancelHotelMenu() {
        while (true) {
            System.out.println("\n=== CANCEL HOTEL BOOKING ===");
            System.out.println("1. View Active Hotel Bookings");
            System.out.println("2. Cancel Specific Hotel Booking");
            System.out.println("3. Cancel All Hotel Bookings");
            System.out.println("4. Cancel Hotel Booking by ID");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    displayActiveHotelBookings();
                    break;
                case 2:
                    cancelSpecificHotelBooking();
                    break;
                case 3:
                    cancelAllHotelBookings();
                    break;
                case 4:
                    cancelHotelBookingById();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void displayActiveHotelBookings() {
        loadActiveHotelBookings();
        
        if (activeHotelBookings.isEmpty()) {
            System.out.println("\nNo active hotel bookings found.");
            return;
        }
        
        System.out.println("\n=== ACTIVE HOTEL BOOKINGS ===");
        System.out.println("=".repeat(60));
        
        for (int i = 0; i < activeHotelBookings.size(); i++) {
            Booking booking = activeHotelBookings.get(i);
            System.out.println("\n[" + (i + 1) + "] Booking ID: " + booking.getId());
            System.out.println("    Pet: " + booking.getPetName());
            System.out.println("    Service: " + booking.getServiceName());
            System.out.println("    Price: ₱" + booking.getServicePrice());
            
            if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
                System.out.println("    Details: " + booking.getSpecialRequests());
            }
            
            double refund = booking.getServicePrice() * (1 - HOTEL_CANCEL_FEE);
            System.out.println("    Refund if cancelled: ₱" + refund);
            System.out.println("-".repeat(40));
        }
    }
    
    private void cancelSpecificHotelBooking() {
        loadActiveHotelBookings();
        
        if (activeHotelBookings.isEmpty()) {
            System.out.println("\nNo active hotel bookings to cancel.");
            return;
        }
        
        displayActiveHotelBookings();
        
        System.out.print("\nSelect hotel booking to cancel (1-" + activeHotelBookings.size() + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice < 1 || choice > activeHotelBookings.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Booking selected = activeHotelBookings.get(choice - 1);
        processCancellation(selected);
    }
    
    private void cancelAllHotelBookings() {
        loadActiveHotelBookings();
        
        if (activeHotelBookings.isEmpty()) {
            System.out.println("\nNo active hotel bookings to cancel.");
            return;
        }
        
        System.out.println("\n=== ALL HOTEL BOOKINGS ===");
        double totalCost = 0;
        double totalRefund = 0;
        
        for (Booking booking : activeHotelBookings) {
            System.out.println("\nBooking ID: " + booking.getId());
            System.out.println("  Pet: " + booking.getPetName());
            System.out.println("  Service: " + booking.getServiceName());
            System.out.println("  Price: ₱" + booking.getServicePrice());
            
            totalCost += booking.getServicePrice();
            totalRefund += booking.getServicePrice() * (1 - HOTEL_CANCEL_FEE);
        }
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Total Bookings: " + activeHotelBookings.size());
        System.out.println("Total Cost: ₱" + totalCost);
        System.out.println("Total Refund: ₱" + totalRefund);
        System.out.println("Cancellation Fee: ₱" + (totalCost * HOTEL_CANCEL_FEE));
        
        System.out.print("\nCancel ALL hotel bookings? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancellation aborted.");
            return;
        }
        
        int cancelled = 0;
        for (Booking booking : activeHotelBookings) {
            if (dbManager.cancelBooking(booking.getId())) {
                cancelled++;
            }
        }
        
        System.out.println("\n✓ Successfully cancelled " + cancelled + " out of " + activeHotelBookings.size() + " hotel bookings.");
        System.out.println("Total Refund: ₱" + totalRefund);
        System.out.println("Refund will be processed within 5-7 business days.");
        
        loadActiveHotelBookings();
    }
    
    private void cancelHotelBookingById() {
        loadActiveHotelBookings();
        
        if (activeHotelBookings.isEmpty()) {
            System.out.println("\nNo active hotel bookings found.");
            return;
        }
        
        System.out.println("\n=== ACTIVE HOTEL BOOKINGS ===");
        for (Booking booking : activeHotelBookings) {
            System.out.println("Booking ID: " + booking.getId() + " - Pet: " + booking.getPetName());
        }
        
        System.out.print("\nEnter Booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();
        
        Booking selected = null;
        for (Booking booking : activeHotelBookings) {
            if (booking.getId() == bookingId) {
                selected = booking;
                break;
            }
        }
        
        if (selected == null) {
            System.out.println("Booking ID not found or not a hotel booking.");
            return;
        }
        
        processCancellation(selected);
    }
    
    private void processCancellation(Booking booking) {
        System.out.println("\n=== HOTEL BOOKING DETAILS ===");
        System.out.println("Booking ID: " + booking.getId());
        System.out.println("Pet: " + booking.getPetName());
        System.out.println("Service: " + booking.getServiceName());
        System.out.println("Price: ₱" + booking.getServicePrice());
        
        double refundAmount = booking.getServicePrice() * (1 - HOTEL_CANCEL_FEE);
        double cancelFee = booking.getServicePrice() * HOTEL_CANCEL_FEE;
        
        System.out.println("\n=== CANCELLATION SUMMARY ===");
        System.out.println("Original Price: ₱" + booking.getServicePrice());
        System.out.println("Cancellation Fee (10%): ₱" + cancelFee);
        System.out.println("Refund Amount: ₱" + refundAmount);
        
        System.out.print("\nAre you sure you want to cancel this hotel booking? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancellation aborted.");
            return;
        }
        
        boolean success = dbManager.cancelBooking(booking.getId());
        
        if (success) {
            System.out.println("\n✓ HOTEL BOOKING CANCELLED SUCCESSFULLY");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("Pet: " + booking.getPetName());
            System.out.println("Refund Amount: ₱" + refundAmount);
            System.out.println("Refund will be processed within 5-7 business days.");
            
            loadActiveHotelBookings();
        } else {
            System.out.println("Failed to cancel hotel booking. Please try again.");
        }
    }
    
    public boolean hasActiveHotelBookings() {
        loadActiveHotelBookings();
        return !activeHotelBookings.isEmpty();
    }
    
    public int getActiveHotelBookingCount() {
        loadActiveHotelBookings();
        return activeHotelBookings.size();
    }
}