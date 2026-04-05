package PetSpaHotel;

public class Room {
    private int id;
    private String roomType;
    private double pricePerNight;
    private boolean isAvailable;
    private String description;
    
    public Room(int id, String roomType, double pricePerNight, boolean isAvailable, String description) {
        this.id = id;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.isAvailable = isAvailable;
        this.description = description;
    }
    
    public int getId() { return id; }
    public String getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public String getDescription() { return description; }
    
    public void setId(int id) { this.id = id; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
    public void setDescription(String description) { this.description = description; }
    
    public void displayRoomInfo() {
        System.out.println("\n=== ROOM INFORMATION ===");
        System.out.println("Room Type: " + roomType);
        System.out.println("Price per Night: ₱" + pricePerNight);
        System.out.println("Status: " + (isAvailable ? "Available" : "Not Available"));
        System.out.println("Description: " + description);
    }
}