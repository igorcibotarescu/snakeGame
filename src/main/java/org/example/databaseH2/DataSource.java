package org.example.databaseH2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DataSource {

    private static final Logger LOGGER = LogManager.getLogger(DataSource.class);
    private static final String user = System.getenv("SNAKE_DB_USER");
    private static final String password = System.getenv("SNAKE_DB_PASSWORD");
    private static final JdbcConnectionPool cp;

    static {
        final String dbUrl = "jdbc:h2:" + getAppDataFolder("snakeDb", "snakeDbFolder");
        if (user == null || password == null) {
            throw new IllegalStateException("Database credentials not set in environment variables.");
        }
        cp = JdbcConnectionPool.create(dbUrl, user, password);
        cp.setMaxConnections(100);
    }

    public static void close(){
        cp.dispose();
    }

    public static Connection getConnection() throws SQLException {
        return cp.getConnection();
    }

    public static void hideFolder(Path folder) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Files.setAttribute(folder, "dos:hidden", true);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to hide folder: {}", folder);
        }
    }

    public static Path getAppDataFolder(String dbName, String dbFolderName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appData = System.getenv("LOCALAPPDATA") + "\\" + dbFolderName;
            return Paths.get(appData, dbName);
        }
        return null; //TODO: add support for all OS
    }
}
