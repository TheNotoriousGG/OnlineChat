package serverside.interfaces;

import java.sql.SQLException;

public interface AuthService {
    void start();
    void stop();
    String getNickByLoginPassword(String login,String pass) throws SQLException, ClassNotFoundException;

}
