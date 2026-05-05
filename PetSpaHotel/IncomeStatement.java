package PetSpaHotel;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class IncomeStatement {
    private DatabaseManager dbManager;
    private int userId;
    
    public IncomeStatement(DatabaseManager dbManager, int userId) {
        this.dbManager = dbManager;
        this.userId = userId;
    }
    
    public void displayIncomeStatement() {
        List<Booking> bookings = dbManager.getUserBookings(userId);
        
        if (bookings.isEmpty()) {
            System.out.println("\nNo transactions found.");
            return;
        }
        
        double totalSpent = 0;
        double totalHotelSpent = 0;
        double totalSpaSpent = 0;
        int hotelCount = 0;
        int spaCount = 0;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    INCOME STATEMENT                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("\n--- TRANSACTION HISTORY ---");
        
        for (Booking booking : bookings) {
            double price = booking.getServicePrice();
            totalSpent += price;
            
            if (booking.getBookingType().equalsIgnoreCase("Hotel")) {
                totalHotelSpent += price;
                hotelCount++;
            } else if (booking.getBookingType().equalsIgnoreCase("Spa")) {
                totalSpaSpent += price;
                spaCount++;
            }
            
            System.out.println("\n[" + booking.getBookingType() + "] " + booking.getServiceName());
            System.out.println("   Pet: " + booking.getPetName());
            System.out.println("   Amount: ₱" + String.format("%.2f", price));
            System.out.println("   Date: " + booking.getBookingDate());
            System.out.println("   Status: " + booking.getStatus());
            System.out.println("   Payment: " + booking.getPaymentStatus());
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SUMMARY                                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("Total Hotel Bookings: " + hotelCount);
        System.out.println("Total Spa Bookings: " + spaCount);
        System.out.println("Total Hotel Spending: ₱" + String.format("%.2f", totalHotelSpent));
        System.out.println("Total Spa Spending: ₱" + String.format("%.2f", totalSpaSpent));
        System.out.println("TOTAL SPENT: ₱" + String.format("%.2f", totalSpent));
        
        double vatCollected = totalSpent * (PaymentFramework.VAT_RATE / (1 + PaymentFramework.VAT_RATE));
        System.out.println("\nVAT Collected: ₱" + String.format("%.2f", vatCollected));
        System.out.println("Net Sales (excl. VAT): ₱" + String.format("%.2f", totalSpent - vatCollected));
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    END OF STATEMENT                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}