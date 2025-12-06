package test;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/duck_social_network_intelij";
        String user = "myappuser";
        String password = "1234";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connection successful!");
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}