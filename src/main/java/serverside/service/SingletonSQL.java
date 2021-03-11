package serverside.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SingletonSQL {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB = "jdbc:mysql://localhost/onlinechat";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection connection;


    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        if (connection == null) {
            connection = initConnection();
        }
        return connection;
    }

    private static Connection initConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(DB, USER, PASSWORD);
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
