package com.crisis.crisismanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final DataSource dataSource;
    private final Random rand = new Random();

    public DataSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    final double[][] CITIES = {{41.0082, 28.9784}, {39.9334, 32.8597}, {38.4192, 27.1287}, {37.0000, 35.3213}, {40.1885, 29.0610}, {39.7767, 30.5206}, {37.8746, 32.4932}, {36.8969, 30.7133}, {41.0015, 39.7178}};
    final String[] CATEGORIES = {"Food", "Water", "Medicine", "Shelter", "Clothing", "Rescue", "Medical Aid", "Transportation", "Power Supply", "Communication"};
    final String[] ROLES = {"Victim", "Volunteer", "Operator"};
    final String[] STATUSES = {"Pending", "InProgress", "Completed", "Cancelled"};
    final String[] FIRST_NAMES = {"Ali", "Ayşe", "Mehmet", "Fatma", "Mustafa", "Zeynep", "Ahmet", "Elif"};
    final String[] LAST_NAMES = {"Yılmaz", "Kaya", "Demir", "Şahin", "Çelik", "Arslan", "Doğan", "Aydın"};
    final String[] DESCRIPTIONS = {"Urgent food needed", "Clean water supply required", "Medical assistance needed", "Temporary shelter requested"};

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            if (getCount(conn, "Users") > 0) {
                System.out.println(">> Veritabanı dolu, DataSeeder iptal edildi.");
                return;
            }

            conn.setAutoCommit(false);

            try {
                System.out.println(">> Veriler ekleniyor...");

                int userCount = 2000;
                insertUsers(conn, userCount);

                int maxUserId = getMaxId(conn, "Users", "UserID");
                insertRequests(conn, 1500, maxUserId);
                insertResources(conn, 1000, maxUserId);

                int maxReqId = getMaxId(conn, "Request", "RequestID");
                int maxResId = getMaxId(conn, "Resources", "ResourceID");
                insertMatches(conn, 500, maxReqId, maxResId);

                conn.commit();
                System.out.println(">> İşlem başarıyla tamamlandı.");
            } catch (Exception e) {
                conn.rollback();
                System.err.println(">> HATA OLUŞTU: " + e.getMessage());
                throw e;
            }
        }
    }

    private void insertUsers(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO Users (UserName, PhoneNumber, UserRole, IdentityNumber, UserPassword, UserLocation) " +
                "VALUES (?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                double[] city = CITIES[rand.nextInt(CITIES.length)];
                ps.setString(1, randomName());
                ps.setString(2, randomPhone());
                ps.setString(3, ROLES[rand.nextInt(ROLES.length)]);
                ps.setString(4, randomTC());
                ps.setString(5, "pass123");
                ps.setDouble(6, city[1] + rand.nextGaussian() * 0.05);
                ps.setDouble(7, city[0] + rand.nextGaussian() * 0.05);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("   " + count + " kullanıcı eklendi.");
        }
    }

    private void insertRequests(Connection conn, int count, int maxUserId) throws SQLException {
        String sql = "INSERT INTO Request (VictimID, Category, UrgencyLevel, Status, Description, Times, VulnerableCount, RequestLocation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                double[] city = CITIES[rand.nextInt(CITIES.length)];
                ps.setInt(1, rand.nextInt(maxUserId) + 1);
                ps.setString(2, CATEGORIES[rand.nextInt(CATEGORIES.length)]);
                ps.setInt(3, rand.nextInt(3) + 1); // ← 1-3 arası urgency level
                ps.setString(4, STATUSES[rand.nextInt(STATUSES.length)]);
                ps.setString(5, DESCRIPTIONS[rand.nextInt(DESCRIPTIONS.length)]);
                ps.setTimestamp(6, randomTimestamp());
                ps.setInt(7, rand.nextInt(10) + 1);
                ps.setDouble(8, city[1] + rand.nextGaussian() * 0.05);
                ps.setDouble(9, city[0] + rand.nextGaussian() * 0.05);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("   " + count + " talep eklendi.");
        }
    }

    private void insertResources(Connection conn, int count, int maxUserId) throws SQLException {
        String sql = "INSERT INTO Resources (ProviderID, Category, InitialQuantity, CurrentQuantity, ResourceLocation) " +
                "VALUES (?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                double[] city = CITIES[rand.nextInt(CITIES.length)];
                int init = rand.nextInt(100) + 10;
                ps.setInt(1, rand.nextInt(maxUserId) + 1);
                ps.setString(2, CATEGORIES[rand.nextInt(CATEGORIES.length)]);
                ps.setInt(3, init);
                ps.setInt(4, rand.nextInt(init));
                ps.setDouble(5, city[1] + rand.nextGaussian() * 0.05);
                ps.setDouble(6, city[0] + rand.nextGaussian() * 0.05);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("   " + count + " kaynak eklendi.");
        }
    }

    private void insertMatches(Connection conn, int count, int maxReqId, int maxResId) throws SQLException {
        String sql = "INSERT INTO RequestResourceMatches (RequestID, ResourceID, MatchDate, AllocateQuantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                ps.setInt(1, rand.nextInt(maxReqId) + 1);
                ps.setInt(2, rand.nextInt(maxResId) + 1);
                ps.setTimestamp(3, randomTimestamp());
                ps.setInt(4, rand.nextInt(50) + 1);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("   " + count + " eşleşme eklendi.");
        }
    }

    private int getCount(Connection conn, String table) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getMaxId(Connection conn, String table, String col) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT MAX(" + col + ") FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private String randomName() { return FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + " " + LAST_NAMES[rand.nextInt(LAST_NAMES.length)]; }
    private String randomPhone() { return "05" + (rand.nextInt(900000000) + 100000000); }
    private String randomTC() {
        StringBuilder tc = new StringBuilder();
        tc.append(rand.nextInt(9) + 1);
        for (int i = 0; i < 10; i++) {
            tc.append(rand.nextInt(10));
        }
        return tc.toString();
    }
    private Timestamp randomTimestamp() { return new Timestamp(System.currentTimeMillis() - (long)(rand.nextDouble() * 1000000000L)); }
}