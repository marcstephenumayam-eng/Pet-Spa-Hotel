
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:petshop.db";
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        createTables();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void createTables() {
        System.out.println("Initializing database...");
        
        // Users table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        // Pets table
        String createPetsTable = "CREATE TABLE IF NOT EXISTS pets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "breed TEXT NOT NULL," +
                "age INTEGER NOT NULL," +
                "health_condition TEXT," +
                "special_notes TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";
        
        // Bookings table
        String createBookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pet_id INTEGER NOT NULL," +
                "booking_type TEXT NOT NULL," +
                "service_details TEXT," +
                "status TEXT DEFAULT 'active'," +
                "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE" +
                ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createUsersTable);
            System.out.println("✓ Users table ready");
            
            stmt.execute(createPetsTable);
            System.out.println("✓ Pets table ready");
            
            stmt.execute(createBookingsTable);
            System.out.println("✓ Bookings table ready");
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== USER OPERATIONS ====================
    
    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Username already exists!");
            } else {
                System.err.println("Error creating user: " + e.getMessage());
            }
            return false;
        }
    }
    
    public User authenticateUser(String username, String password) {
        String sql = "SELECT id, username, password FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }
    
    // ==================== PET OPERATIONS ====================
    
    public boolean savePet(int userId, Pet pet) {
        String sql = "INSERT INTO pets(user_id, name, breed, age, health_condition, special_notes) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, pet.getName());
            pstmt.setString(3, pet.getBreed());
            pstmt.setInt(4, pet.getAge());
            pstmt.setString(5, pet.getHealthCondition());
            pstmt.setString(6, pet.getSpecialNotes());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                pet.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving pet: " + e.getMessage());
            return false;
        }
    }
    
    public List<Pet> getUserPets(int userId) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM pets WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Pet pet = new Pet(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("breed"),
                    rs.getInt("age"),
                    rs.getString("health_condition")
                );
                pet.setSpecialNotes(rs.getString("special_notes"));
                pets.add(pet);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pets: " + e.getMessage());
        }
        return pets;
    }
    
    public boolean updatePetHealthCondition(int petId, String healthCondition) {
        String sql = "UPDATE pets SET health_condition = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, healthCondition);
            pstmt.setInt(2, petId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating pet health: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== BOOKING OPERATIONS ====================
    
    public boolean createBooking(int petId, String serviceDetails) {
        String sql = "INSERT INTO bookings(pet_id, booking_type, service_details) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            
            // Determine booking type
            if (serviceDetails.startsWith("Hotel")) {
                pstmt.setString(2, "Hotel");
            } else {
                pstmt.setString(2, "Spa");
            }
            
            pstmt.setString(3, serviceDetails);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }
    
    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, p.name as pet_name " +
                     "FROM bookings b " +
                     "JOIN pets p ON b.pet_id = p.id " +
                     "WHERE p.user_id = ? " +
                     "ORDER BY b.booking_date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("pet_id"),
                    rs.getString("pet_name"),
                    rs.getString("service_details"),
                    rs.getString("booking_date"),
                    rs.getString("status")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings: " + e.getMessage());
        }
        return bookings;
    }
    
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'cancelled' WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }
}
