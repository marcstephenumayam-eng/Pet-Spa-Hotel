package PetSpaHotel;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CancelSpaService {
    private DatabaseManager dbManager;
    private Scanner scanner;
    private User currentUser;
    private List<Booking> activeSpaBookings;
    
    private static final double CANCEL_FEE_24_HOURS = 0.25;
    private static final double CANCEL_FEE_48_HOURS = 0.10;
    private static final long FULL_REFUND_DAYS = 2;
    
    public CancelSpaService(DatabaseManager dbManager, Scanner scanner, User currentUser) {
        this.dbManager = dbManager;
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.activeSpaBookings = new ArrayList<>();
        loadActiveSpaBookings();
    }
    
    private void loadActiveSpaBookings() {
        List<Booking> allBookings = dbManager.getUserBookings(currentUser.getId());
        activeSpaBookings.clear();
        
        for (Booking booking : allBookings) {
            if (booking.getBookingType().equalsIgnoreCase("Spa") && 
                booking.getStatus().equalsIgnoreCase("active")) {
                activeSpaBookings.add(booking);
            }
        }
    }
    
    public void showCancelSpaMenu() {
        while (true) {
            System.out.println("\n=== CANCEL SPA SERVICE ===");
            System.out.println("1. View Active Spa Bookings");
            System.out.println("2. Cancel Specific Spa Booking");
            System.out.println("3. Cancel Multiple Spa Bookings");
            System.out.println("4. View Cancellation History");
            System.out.println("5. Check Refund Eligibility");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    displayActiveBookings();
                    break;
                case 2:
                    cancelSingleBooking();
                    break;
                case 3:
                    cancelMultipleBookings();
                    break;
                case 4:
                    viewCancellationHistory();
                    break;
                case 5:
                    checkRefundEligibility();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void displayActiveBookings() {
        loadActiveSpaBookings();
        
        if (activeSpaBookings.isEmpty()) {
            System.out.println("\nNo active spa bookings found.");
            return;
        }
        
        System.out.println("\n=== ACTIVE SPA BOOKINGS ===");
        for (int i = 0; i < activeSpaBookings.size(); i++) {
            Booking booking = activeSpaBookings.get(i);
            System.out.println("\n[" + (i + 1) + "] Booking ID: " + booking.getId());
            System.out.println("    Pet: " + booking.getPetName());
            System.out.println("    Service: " + booking.getServiceName());
            
            String appointmentDate = booking.getAppointmentDate();
            String appointmentTime = booking.getAppointmentTime();
            if (appointmentDate != null && appointmentTime != null) {
                System.out.println("    Date: " + appointmentDate + " at " + appointmentTime);
            } else {
                System.out.println("    Date: Not scheduled");
            }
            
            System.out.println("    Price: ₱" + booking.getServicePrice());
            
            double refundAmount = calculateRefundAmount(booking);
            if (refundAmount > 0) {
                System.out.println("    Eligible Refund: ₱" + refundAmount);
            } else {
                System.out.println("    Eligible Refund: No refund available");
            }
        }
    }
    
    private void cancelSingleBooking() {
        loadActiveSpaBookings();
        
        if (activeSpaBookings.isEmpty()) {
            System.out.println("\nNo active spa bookings found to cancel.");
            return;
        }
        
        displayActiveBookings();
        
        System.out.print("\nEnter the booking number to cancel (1-" + activeSpaBookings.size() + "): ");
        int selection = scanner.nextInt();
        scanner.nextLine();
        
        if (selection < 1 || selection > activeSpaBookings.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Booking selectedBooking = activeSpaBookings.get(selection - 1);
        processCancellation(selectedBooking);
    }
    
    private void cancelMultipleBookings() {
        loadActiveSpaBookings();
        
        if (activeSpaBookings.isEmpty()) {
            System.out.println("\nNo active spa bookings found to cancel.");
            return;
        }
        
        displayActiveBookings();
        
        System.out.print("\nEnter booking numbers to cancel (comma-separated, e.g., 1,3,5): ");
        String input = scanner.nextLine();
        String[] selections = input.split(",");
        
        List<Booking> bookingsToCancel = new ArrayList<>();
        
        for (String selection : selections) {
            try {
                int index = Integer.parseInt(selection.trim()) - 1;
                if (index >= 0 && index < activeSpaBookings.size()) {
                    bookingsToCancel.add(activeSpaBookings.get(index));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection: " + selection);
            }
        }
        
        if (bookingsToCancel.isEmpty()) {
            System.out.println("No valid bookings selected.");
            return;
        }
        
        System.out.println("\n=== CONFIRM BATCH CANCELLATION ===");
        double totalRefund = 0;
        for (Booking booking : bookingsToCancel) {
            double refund = calculateRefundAmount(booking);
            totalRefund += refund;
            System.out.println(booking.getPetName() + " - " + booking.getServiceName() + ": Refund ₱" + refund);
        }
        
        System.out.println("Total Refund Amount: ₱" + totalRefund);
        System.out.print("\nAre you sure you want to cancel these " + bookingsToCancel.size() + " booking(s)? (y/n): ");
        
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Batch cancellation cancelled.");
            return;
        }
        
        int successCount = 0;
        for (Booking booking : bookingsToCancel) {
            if (processCancellationWithoutConfirmation(booking)) {
                successCount++;
            }
        }
        
        System.out.println("Successfully cancelled " + successCount + " out of " + bookingsToCancel.size() + " booking(s).");
        loadActiveSpaBookings();
    }
    
    private void processCancellation(Booking booking) {
        System.out.println("\n=== BOOKING DETAILS ===");
        booking.displayBookingInfo();
        
        double refundAmount = calculateRefundAmount(booking);
        double cancellationFee = booking.getServicePrice() - refundAmount;
        
        System.out.println("\n=== CANCELLATION SUMMARY ===");
        System.out.println("Original Price: ₱" + booking.getServicePrice());
        
        if (cancellationFee > 0) {
            System.out.println("Cancellation Fee: ₱" + cancellationFee);
            System.out.println("Refund Amount: ₱" + refundAmount);
        } else {
            System.out.println("Cancellation Fee: ₱0.00");
            System.out.println("Full Refund: ₱" + refundAmount);
        }
        
        System.out.print("\nAre you sure you want to cancel this booking? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancellation aborted.");
            return;
        }
        
        if (booking.getAppointmentDate() != null && isLateCancellation(booking.getAppointmentDate())) {
            System.out.println("\nWARNING: This is a late cancellation.");
            System.out.println("You will be charged a 25% cancellation fee.");
            System.out.print("Do you still want to proceed? (y/n): ");
            String lateConfirm = scanner.nextLine();
            if (!lateConfirm.equalsIgnoreCase("y")) {
                System.out.println("Cancellation aborted.");
                return;
            }
        }
        
        boolean success = dbManager.cancelBooking(booking.getId());
        
        if (success) {
            System.out.println("\nBOOKING CANCELLED SUCCESSFULLY");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("Service: " + booking.getServiceName() + " for " + booking.getPetName());
            
            if (refundAmount > 0) {
                System.out.println("Refund Amount: ₱" + refundAmount);
                System.out.println("Refund will be processed within 5-7 business days.");
            } else {
                System.out.println("No refund is available for this cancellation.");
            }
            
            loadActiveSpaBookings();
        } else {
            System.out.println("Failed to cancel booking. Please try again.");
        }
    }
    
    private boolean processCancellationWithoutConfirmation(Booking booking) {
        return dbManager.cancelBooking(booking.getId());
    }
    
    private double calculateRefundAmount(Booking booking) {
        if (booking.getAppointmentDate() == null || booking.getAppointmentDate().isEmpty()) {
            return 0;
        }
        
        try {
            LocalDate appointmentDate = LocalDate.parse(booking.getAppointmentDate());
            LocalDate today = LocalDate.now();
            
            long daysUntilAppointment = ChronoUnit.DAYS.between(today, appointmentDate);
            
            if (daysUntilAppointment >= FULL_REFUND_DAYS) {
                return booking.getServicePrice();
            } else if (daysUntilAppointment >= 1) {
                return booking.getServicePrice() * (1 - CANCEL_FEE_48_HOURS);
            } else if (daysUntilAppointment == 0) {
                return booking.getServicePrice() * (1 - CANCEL_FEE_24_HOURS);
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
    
    private boolean isLateCancellation(String appointmentDate) {
        if (appointmentDate == null || appointmentDate.isEmpty()) {
            return false;
        }
        
        try {
            LocalDate aptDate = LocalDate.parse(appointmentDate);
            LocalDate today = LocalDate.now();
            long daysUntil = ChronoUnit.DAYS.between(today, aptDate);
            return daysUntil == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void viewCancellationHistory() {
        List<Booking> allBookings = dbManager.getUserBookings(currentUser.getId());
        List<Booking> cancelledBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            if (booking.getBookingType().equalsIgnoreCase("Spa") && 
                booking.getStatus().equalsIgnoreCase("cancelled")) {
                cancelledBookings.add(booking);
            }
        }
        
        if (cancelledBookings.isEmpty()) {
            System.out.println("\nNo cancellation history found.");
            return;
        }
        
        System.out.println("\n=== SPA CANCELLATION HISTORY ===");
        for (int i = 0; i < cancelledBookings.size(); i++) {
            Booking booking = cancelledBookings.get(i);
            System.out.println("\n[" + (i + 1) + "] Cancelled Booking");
            System.out.println("    Booking ID: " + booking.getId());
            System.out.println("    Pet: " + booking.getPetName());
            System.out.println("    Service: " + booking.getServiceName());
            
            if (booking.getAppointmentDate() != null && booking.getAppointmentTime() != null) {
                System.out.println("    Original Appointment: " + booking.getAppointmentDate() + " at " + booking.getAppointmentTime());
            } else {
                System.out.println("    Original Appointment: Not scheduled");
            }
            
            System.out.println("    Price: ₱" + booking.getServicePrice());
            System.out.println("    Cancelled on: " + booking.getBookingDate());
        }
    }
    
    private void checkRefundEligibility() {
        loadActiveSpaBookings();
        
        if (activeSpaBookings.isEmpty()) {
            System.out.println("\nNo active spa bookings found.");
            return;
        }
        
        System.out.println("\n=== REFUND ELIGIBILITY ===");
        double totalPotentialRefund = 0;
        
        for (int i = 0; i < activeSpaBookings.size(); i++) {
            Booking booking = activeSpaBookings.get(i);
            double refund = calculateRefundAmount(booking);
            totalPotentialRefund += refund;
            
            System.out.println("\n[" + (i + 1) + "] " + booking.getPetName() + " - " + booking.getServiceName());
            
            if (booking.getAppointmentDate() != null && booking.getAppointmentTime() != null) {
                System.out.println("    Appointment: " + booking.getAppointmentDate() + " at " + booking.getAppointmentTime());
            } else {
                System.out.println("    Appointment: Not scheduled");
            }
            
            System.out.println("    Service Price: ₱" + booking.getServicePrice());
            
            if (refund == booking.getServicePrice()) {
                System.out.println("    Status: Eligible for FULL REFUND");
                System.out.println("    Refund Amount: ₱" + refund);
            } else if (refund > 0) {
                System.out.println("    Status: Eligible for PARTIAL REFUND");
                System.out.println("    Refund Amount: ₱" + refund);
            } else {
                System.out.println("    Status: NOT ELIGIBLE for refund");
            }
        }
        
        System.out.println("\nTotal Potential Refund: ₱" + totalPotentialRefund);
        System.out.println("\nRefund Policy:");
        System.out.println("  Cancel 2+ days before: 100% refund");
        System.out.println("  Cancel 1 day before: 10% cancellation fee");
        System.out.println("  Cancel on appointment day: 25% cancellation fee");
        System.out.println("  Cancel after appointment: No refund");
    }
    
    public boolean hasActiveSpaBookings() {
        loadActiveSpaBookings();
        return !activeSpaBookings.isEmpty();
    }
}