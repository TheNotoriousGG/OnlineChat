package serverside.interfaces;

public interface AuthService {
    void start();
    void stop();
    String getNickByLoginPassword(String login,String pass);

}
