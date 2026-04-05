package PetSpaHotel;

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
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "is_admin INTEGER DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
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
        
        String createBookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pet_id INTEGER NOT NULL," +
                "booking_type TEXT NOT NULL," +
                "service_name TEXT," +
                "service_price REAL," +
                "appointment_date TEXT," +
                "appointment_time TEXT," +
                "special_requests TEXT," +
                "status TEXT DEFAULT 'active'," +
                "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE" +
                ")";
        
        String createSpaServicesTable = "CREATE TABLE IF NOT EXISTS spa_services (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "service_name TEXT NOT NULL," +
                "description TEXT," +
                "price REAL NOT NULL," +
                "duration INTEGER," +
                "is_active INTEGER DEFAULT 1" +
                ")";
        
        String createRoomsTable = "CREATE TABLE IF NOT EXISTS rooms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "room_type TEXT NOT NULL UNIQUE," +
                "price_per_night REAL NOT NULL," +
                "is_available INTEGER DEFAULT 1," +
                "description TEXT" +
                ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createUsersTable);
            stmt.execute(createPetsTable);
            stmt.execute(createBookingsTable);
            stmt.execute(createSpaServicesTable);
            stmt.execute(createRoomsTable);
            
            initializeSpaServices();
            initializeRooms();
            initializeAdminUser();
            
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
    
    private void initializeSpaServices() {
        String checkSql = "SELECT COUNT(*) FROM spa_services";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO spa_services (service_name, description, price, duration) VALUES " +
                        "('Basic Grooming', 'Bath, brush, nail trim, and ear cleaning', 499, 60), " +
                        "('Full Grooming', 'Complete grooming including haircut and styling', 999, 90), " +
                        "('Massage Therapy', 'Relaxing massage for your pet', 1099, 45), " +
                        "('Dental Cleaning', 'Professional teeth cleaning and oral care', 999, 30), " +
                        "('Luxury Spa Package', 'Full grooming, massage, and dental cleaning combo', 1499, 120)";
                
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error initializing spa services: " + e.getMessage());
        }
    }
    
    private void initializeRooms() {
        String checkSql = "SELECT COUNT(*) FROM rooms";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO rooms (room_type, price_per_night, description) VALUES " +
                        "('Standard', 1499, 'Comfortable room with basic amenities'), " +
                        "('Deluxe', 2999, 'Spacious room with premium amenities'), " +
                        "('VIP Suite', 4999, 'Luxury suite with exclusive services')";
                
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error initializing rooms: " + e.getMessage());
        }
    }
    
    private void initializeAdminUser() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE is_admin = 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO users (username, password, is_admin) VALUES ('admin', 'admin123', 1)";
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error initializing admin user: " + e.getMessage());
        }
    }
    
    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users(username, password, is_admin) VALUES(?, ?, 0)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public User authenticateUser(String username, String password) {
        String sql = "SELECT id, username, password, is_admin FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"), 
                    rs.getString("username"), 
                    rs.getString("password"),
                    rs.getInt("is_admin") == 1
                );
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }
    
    public boolean savePet(int userId, Pet pet) {
        String sql = "INSERT INTO pets(user_id, name, breed, age, health_condition, special_notes) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, pet.getName());
            pstmt.setString(3, pet.getBreed());
            pstmt.setInt(4, pet.getAge());
            pstmt.setString(5, pet.getHealthCondition());
            pstmt.setString(6, pet.getSpecialNotes());
            pstmt.executeUpdate();
            
            String getIdSql = "SELECT last_insert_rowid()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(getIdSql)) {
                if (rs.next()) {
                    pet.setId(rs.getInt(1));
                }
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
    
    public boolean deletePet(int petId) {
        String checkBookingsSql = "SELECT COUNT(*) FROM bookings WHERE pet_id = ? AND status = 'active'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(checkBookingsSql)) {
            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Cannot delete pet with active bookings. Please cancel bookings first.");
                return false;
            }
            
            String deleteSql = "DELETE FROM pets WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, petId);
                int rowsAffected = deleteStmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting pet: " + e.getMessage());
            return false;
        }
    }
    
    public List<SpaService> getAvailableSpaServices() {
        List<SpaService> services = new ArrayList<>();
        String sql = "SELECT * FROM spa_services WHERE is_active = 1 ORDER BY price";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                SpaService service = new SpaService(
                    rs.getInt("id"),
                    rs.getString("service_name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("duration")
                );
                services.add(service);
            }
        } catch (SQLException e) {
            System.err.println("Error getting spa services: " + e.getMessage());
        }
        return services;
    }
    
    public List<SpaService> getAllSpaServices() {
        List<SpaService> services = new ArrayList<>();
        String sql = "SELECT * FROM spa_services ORDER BY price";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                SpaService service = new SpaService(
                    rs.getInt("id"),
                    rs.getString("service_name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("duration")
                );
                services.add(service);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all spa services: " + e.getMessage());
        }
        return services;
    }
    
    public boolean addSpaService(String name, String description, double price, int duration) {
        String sql = "INSERT INTO spa_services (service_name, description, price, duration, is_active) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, duration);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding spa service: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateSpaService(int id, String name, String description, double price, int duration, boolean isActive) {
        String sql = "UPDATE spa_services SET service_name = ?, description = ?, price = ?, duration = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, duration);
            pstmt.setInt(5, isActive ? 1 : 0);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating spa service: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteSpaService(int id) {
        String sql = "DELETE FROM spa_services WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting spa service: " + e.getMessage());
            return false;
        }
    }
    
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY price_per_night";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("id"),
                    rs.getString("room_type"),
                    rs.getDouble("price_per_night"),
                    rs.getInt("is_available") == 1,
                    rs.getString("description")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error getting rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    public boolean addRoom(String roomType, double pricePerNight, String description) {
        String sql = "INSERT INTO rooms (room_type, price_per_night, description, is_available) VALUES (?, ?, ?, 1)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType);
            pstmt.setDouble(2, pricePerNight);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateRoom(int id, String roomType, double pricePerNight, boolean isAvailable, String description) {
        String sql = "UPDATE rooms SET room_type = ?, price_per_night = ?, is_available = ?, description = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType);
            pstmt.setDouble(2, pricePerNight);
            pstmt.setInt(3, isAvailable ? 1 : 0);
            pstmt.setString(4, description);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createSpaBooking(int petId, int serviceId, String appointmentDate, 
                                   String appointmentTime, String specialRequests) {
        String sql = "INSERT INTO bookings(pet_id, booking_type, service_name, service_price, " +
                    "appointment_date, appointment_time, special_requests) " +
                    "VALUES(?, 'Spa', (SELECT service_name FROM spa_services WHERE id = ?), " +
                    "(SELECT price FROM spa_services WHERE id = ?), ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            pstmt.setInt(2, serviceId);
            pstmt.setInt(3, serviceId);
            pstmt.setString(4, appointmentDate);
            pstmt.setString(5, appointmentTime);
            pstmt.setString(6, specialRequests);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating spa booking: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createHotelBooking(int petId, int nights, String roomType, double totalCost) {
        String sql = "INSERT INTO bookings(pet_id, booking_type, service_name, service_price, special_requests) " +
                    "VALUES(?, 'Hotel', ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            pstmt.setString(2, nights + " nights - " + roomType + " room");
            pstmt.setDouble(3, totalCost);
            pstmt.setString(4, "Room Type: " + roomType + ", Nights: " + nights);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating hotel booking: " + e.getMessage());
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
                    rs.getString("booking_type"),
                    rs.getString("service_name"),
                    rs.getDouble("service_price"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("special_requests"),
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
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, p.name as pet_name, u.username as owner_name " +
                     "FROM bookings b " +
                     "JOIN pets p ON b.pet_id = p.id " +
                     "JOIN users u ON p.user_id = u.id " +
                     "ORDER BY b.booking_date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("pet_id"),
                    rs.getString("pet_name"),
                    rs.getString("booking_type"),
                    rs.getString("service_name"),
                    rs.getDouble("service_price"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("special_requests"),
                    rs.getString("booking_date"),
                    rs.getString("status")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bookings: " + e.getMessage());
        }
        return bookings;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, is_admin, created_at FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    "",
                    rs.getInt("is_admin") == 1
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
        }
        return users;
    }
    
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'cancelled' WHERE id = ? AND status = 'active'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }
}