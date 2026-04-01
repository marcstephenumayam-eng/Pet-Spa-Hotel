package PetSpaHotel;

public class SpaService {
    private int id;
    private String name;
    private String description;
    private double price;
    private int duration;
    
    public SpaService(int id, String name, String description, double price, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getDuration() { return duration; }
    
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public void displayServiceInfo() {
        System.out.println("\n=== SPA SERVICE DETAILS ===");
        System.out.println("Service: " + name);
        System.out.println("Description: " + description);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Price: ₱" + price);
    }
}