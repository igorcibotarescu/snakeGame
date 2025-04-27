package org.example.databaseH2;

import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DataSource {

    private static final String user = "snake";
    private static final String password = "snake";
    private static final JdbcConnectionPool cp;

    static {
        final String dbUrl = "jdbc:h2:" + getAppDataFolder("snakeDb", "snakeDbFolder");
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
            System.err.println("Failed to hide folder: " + folder);
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
