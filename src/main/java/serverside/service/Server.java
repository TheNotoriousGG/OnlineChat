package serverside.service;

import serverside.interfaces.AuthService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final Integer PORT = 8085;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    List<ClientHandler> clients;
    AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        System.out.println("Сервер запущен");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();

            while(true) {
                System.out.println("Ожидание подключения...");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключен!");
                new ClientHandler(this,socket);

            }
        } catch (IOException e) {
            System.out.println("Не удалось создать сессию");
        }
    }

    public synchronized boolean isNickBusy(String nick){
        for(ClientHandler c: clients){
            if(c.nick.equalsIgnoreCase(nick)){
                return true;
            }
        }
        return false;
    }

    public synchronized void distributionToAll(String msg,ClientHandler me) {
        for(ClientHandler c: clients){
            c.sendMessage("["+me.nick+"] " + msg);
        }
    }

    public synchronized void privateMessage(ClientHandler me, String nick, String message){
        for(ClientHandler c: clients){
            //System.out.println(c.nick);
            if(c.nick.equalsIgnoreCase(nick)){
                c.sendMessage("[private] "+message);
                me.sendMessage( "[private from " +nick+"] " +message);
            }

        }
    }


    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }
    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

}
