package serverside.service;

import serverside.interfaces.AuthService;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    List<Entry> entries;

    public BaseAuthService(){
        entries = new ArrayList<>();
        entries.add(new Entry("Georgi747","7767406", "TheNotorious"));
        entries.add(new Entry("Polly","12345", "Polly"));
        entries.add(new Entry("Tomas","qwerty", "Shelby"));

    }


    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPassword(String login,String pass) {
        for(Entry e: entries){
            if(e.login.equalsIgnoreCase(login) && e.password.equalsIgnoreCase(pass)){
                return e.nick;
            }
        }
        return null;
    }


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

}
