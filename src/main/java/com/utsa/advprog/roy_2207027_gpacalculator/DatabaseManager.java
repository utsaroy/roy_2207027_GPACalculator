package com.utsa.advprog.roy_2207027_gpacalculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String url = "jdbc:sqlite:database.db";
    public static Connection  getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            //connection failed
            System.out.println("Connection Failed! Check output console");
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void initialize() {
    try (Connection conn = getConnection()) {
        if (conn != null) {
            String sql = "CREATE TABLE IF NOT EXISTS students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "roll TEXT NOT NULL, " +
                    "university TEXT NOT NULL, " +
                    "mobile TEXT, " +
                    "email TEXT, " +
                    "gpa REAL NOT NULL, " +
                    "totalCredits REAL, " +
                    "earnedCredits REAL, " +
                    "coursesData TEXT)";
            
            conn.createStatement().execute(sql);
            System.out.println("Database initialized successfully");
        }
    } catch (SQLException e) {
        //initialization failed
        System.out.println("Database initialization failed");
    }
}
}
