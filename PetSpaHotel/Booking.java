package PetSpaHotel;

public class Booking {
    private int id;
    private int petId;
    private String petName;
    private String bookingType;
    private String serviceName;
    private double servicePrice;
    private String appointmentDate;
    private String appointmentTime;
    private String specialRequests;
    private String bookingDate;
    private String status;
    
    public Booking(int id, int petId, String petName, String bookingType, 
                   String serviceName, double servicePrice, String appointmentDate, 
                   String appointmentTime, String specialRequests, 
                   String bookingDate, String status) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.bookingType = bookingType;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.specialRequests = specialRequests;
        this.bookingDate = bookingDate;
        this.status = status;
    }
    
    public int getId() { return id; }
    public int getPetId() { return petId; }
    public String getPetName() { return petName; }
    public String getBookingType() { return bookingType; }
    public String getServiceName() { return serviceName; }
    public double getServicePrice() { return servicePrice; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getSpecialRequests() { return specialRequests; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    
    public void displayBookingInfo() {
        System.out.println("\n=== BOOKING INFORMATION ===");
        System.out.println("Booking ID: " + id);
        System.out.println("Pet: " + petName);
        System.out.println("Service Type: " + bookingType);
        System.out.println("Service: " + serviceName);
        System.out.println("Price: ₱" + servicePrice);
        
        if (appointmentDate != null && appointmentTime != null) {
            System.out.println("Appointment: " + appointmentDate + " at " + appointmentTime);
        }
        
        if (specialRequests != null && !specialRequests.isEmpty()) {
            System.out.println("Special Requests: " + specialRequests);
        }
        
        System.out.println("Booking Date: " + bookingDate);
        System.out.println("Status: " + status);
    }
}