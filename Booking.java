



public class Booking {
    private int id;
    private int petId;
    private String petName;
    private String bookingType;
    private String bookingDate;
    private String status;
    
    public Booking(int id, int petId, String petName, String bookingType, String bookingDate, String status) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.bookingType = bookingType;
        this.bookingDate = bookingDate;
        this.status = status;
    }
    
    // Getters
    public int getId() { return id; }
    public int getPetId() { return petId; }
    public String getPetName() { return petName; }
    public String getBookingType() { return bookingType; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    
    public void displayBookingInfo() {
        System.out.println("\n=== BOOKING INFORMATION ===");
        System.out.println("Pet: " + petName);
        System.out.println("Service Type: " + bookingType);
        System.out.println("Date: " + bookingDate);
        System.out.println("Status: " + status);
    }
}
