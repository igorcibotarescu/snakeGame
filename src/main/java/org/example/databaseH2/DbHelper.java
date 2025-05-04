package org.example.databaseH2;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.game.User;

import java.sql.*;



public class DbHelper {

    private static final Logger LOGGER = LogManager.getLogger(DbHelper.class);

    static {
        try {
            initDatabase();
        } catch (SQLException e) {
            LOGGER.error("Could not init Db");
        }
    }

    private static void initDatabase() throws SQLException {
        try (Statement statement = DataSource.getConnection().createStatement()) {
            String sql = "Create table if not exists users (userID varchar(50), password varchar(50), score int)";
            statement.execute(sql);
            sql = "Create table if not exists settings (userID varchar(50), settingsObject other)";
            statement.execute(sql);
        }
    }

    public static void saveGame(String userID, Settings settings) throws SQLException {

        String sql;

        if(getSettings(userID) != null){
            sql = "Update settings set userID = ?, settingsObject = ? WHERE userID = " + "'" + userID + "'";
        } else {
            sql = "Insert into settings VALUES (?, ?)";
        }

        try (PreparedStatement preparedStatement = DataSource.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, settings.userID());
            preparedStatement.setObject(2, settings);
            preparedStatement.executeUpdate();
        }
    }

    public static Settings getSettings(String userID) throws SQLException {

        String sql = "SELECT * FROM settings WHERE userID = " + "'" + userID + "'";
        ResultSet resultSet;

        try (Statement statement = DataSource.getConnection().createStatement()) {
            resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return (Settings) resultSet.getObject("settingsObject");
            }
            return  null;
        }
    }

    public static int insertUser(String userID, String password) throws SQLException {

        if(checkUser(userID)) {
            return 0;
        }

        try (Statement statement = DataSource.getConnection().createStatement()) {
            String sql = "Insert into users (userID, password, score) values (" + "'" + userID + "'" + "," + "'" + password + "'" + "," + "'" + 0 + "'" + ")";
            statement.executeUpdate(sql);
            return 1;
        }
    }

    public static void updateScore(String userID, int score) throws SQLException {

        try (Statement statement = DataSource.getConnection().createStatement()) {
            String sql = "Update users set score = " + "'" + score + "'" + " Where userID = " + "'" + userID + "'";
            statement.executeUpdate(sql);
        }
    }

    public static User selectUser(String userID, String password) throws SQLException {

        ResultSet resultSet;
        try (Statement statement = DataSource.getConnection().createStatement()) {
            String sql = "SELECT * FROM users WHERE userID = " + "'" + userID + "'" + " AND password = " + "'" + password + "'";
            resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                return new User(resultSet.getString("password"), resultSet.getInt("score"), resultSet.getString("userID"));
            }
            return null;
        }
    }

    public static User selectUser(String userID) throws SQLException {

        ResultSet resultSet;
        try (Statement statement = DataSource.getConnection().createStatement()) {
            String sql = "SELECT * FROM users WHERE userID = " + "'" + userID + "'";
            resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                return new User(resultSet.getString("password"), resultSet.getInt("score"), resultSet.getString("userID"));
            }
            return null;
        }
    }

    public static boolean checkUser(String userID) throws SQLException {

        ResultSet resultSet;
        try (Statement statement = DataSource.getConnection().createStatement()) {
            String sql = "SELECT * FROM users WHERE userID = " + "'" + userID + "'";
            resultSet = statement.executeQuery(sql);
            return resultSet.next();
        }
    }
}
