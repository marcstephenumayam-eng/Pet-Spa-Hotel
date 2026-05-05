package PetSpaHotel;

public class HotelPayment extends PaymentFramework {
    private int nights;
    private String roomType;
    private double roomRate;
    
    public HotelPayment(int nights, String roomType, double roomRate, int numberOfVisitors, double discountRate) {
        super(roomRate, numberOfVisitors, discountRate);
        this.nights = nights;
        this.roomType = roomType;
        this.roomRate = roomRate;
    }
    
    @Override
    protected double calculateSubtotal() {
        return super.calculateSubtotal() * nights;
    }
    
    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double discounted = discountRate > 0 ? applyDiscount(subtotal) : subtotal;
        return applyVAT(discounted);
    }
    
    public void processHotelPayment(double payment) {
        double total = calculateTotal();
        System.out.println("\n=== HOTEL BOOKING PAYMENT ===");
        System.out.println("Room Type: " + roomType);
        System.out.println("Rate per night: ₱" + String.format("%.2f", roomRate));
        System.out.println("Number of nights: " + nights);
        processInvoice(total, payment);
    }
}