package com.javapowered.nexusai.database;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:nexus-ai.db";

    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public void initialize() {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS conversations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, created_at INTEGER NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, conversation_id INTEGER NOT NULL, " +
                    "role TEXT NOT NULL, content TEXT NOT NULL, created_at INTEGER NOT NULL, " +
                    "FOREIGN KEY(conversation_id) REFERENCES conversations(id))");
        } catch (SQLException e) {
            throw new IllegalStateException("Database initialization failed", e);
        }
    }
}
