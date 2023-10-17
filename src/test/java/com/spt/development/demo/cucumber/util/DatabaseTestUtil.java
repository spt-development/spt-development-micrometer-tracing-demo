package com.spt.development.demo.cucumber.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseTestUtil {
    private DatabaseTestUtil() {}

    public static void clearDatabase(Connection connection) throws SQLException {
        deleteAllBooks(connection);
    }

    public static int deleteAllBooks(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate("DELETE FROM demo.book");
        }
    }
}
