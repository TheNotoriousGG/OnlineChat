package serverside.service;

import serverside.interfaces.AuthService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    private Statement statement;
    private final String query = "SELECT * FROM USERS";
    private ResultSet rs;

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPassword(String login, String pass) {

        try {
            PreparedStatement preparedStatement = SingletonSQL.getConnection().prepareStatement(
                    "SELECT nick FROM users WHERE 1=1 and login = ? and password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            ResultSet rslt = preparedStatement.executeQuery();

            if (rslt.next()) {
                return rslt.getString("nick");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }

/*
    private class Entry{
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }
 */

}
