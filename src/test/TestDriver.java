package test;

public class TestDriver {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL driver loaded!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver not found");
        }
    }
}