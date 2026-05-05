package PetSpaHotel;

public class SpaPayment extends PaymentFramework {
    private String serviceName;
    private int duration;
    
    public SpaPayment(String serviceName, double servicePrice, int duration, int numberOfVisitors, double discountRate) {
        super(servicePrice, numberOfVisitors, discountRate);
        this.serviceName = serviceName;
        this.duration = duration;
    }
    
    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double discounted = discountRate > 0 ? applyDiscount(subtotal) : subtotal;
        return applyVAT(discounted);
    }
    
    public void processSpaPayment(double payment) {
        double total = calculateTotal();
        System.out.println("\n=== SPA SERVICE PAYMENT ===");
        System.out.println("Service: " + serviceName);
        System.out.println("Duration: " + duration + " minutes");
        processInvoice(total, payment);
    }
}