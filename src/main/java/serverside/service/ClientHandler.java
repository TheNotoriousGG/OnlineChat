package serverside.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

public class ClientHandler {

    private Server server;
    private Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    String nick;
    boolean isAuthorized;
    boolean isBlocked;
    int limitSec;
    long start;
    boolean isRecd;
    int limitNoMsg;


    public ClientHandler(Server server, Socket socket) {
        try {
            start = System.currentTimeMillis() / 1000;
            this.server = server;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.isAuthorized = false;
            isBlocked = false;
            this.nick = "";
            limitSec = 120;
            isRecd = false;
            limitNoMsg = 180;


            new Thread(() -> {
                try {
                    Thread.sleep(limitSec * 1000);
                    if (!isAuthorized) {
                        dos.writeUTF("Время ожидания истекло");
                        closeConn();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            new Thread(() -> {
                try {
                    authorisation();
                    communication();

                } catch (IOException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }).start();

        } catch (IOException e) {
            System.out.println("Ошибка обработки клиента: " + e);
        }

    }


    private void communication() throws IOException {
        while (isAuthorized) {
            isRecd = false;
            String msgFromClient = "";

            new Thread(() -> {
                try {
                    Thread.sleep(limitNoMsg * 1000);
                    if (!isRecd) {
                        dos.writeUTF("Вы не отправляли сообщения " + limitNoMsg + " секунд прощайте");
                        closeConn();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            try {
                msgFromClient = dis.readUTF();
                isRecd = true;
            } catch (SocketException ignored) {
                server.unsubscribe(this);
                System.out.println("Клиент закрыл чат...");
                break;
            }


            if (msgFromClient.startsWith("/")) {
                if (msgFromClient.startsWith("/end")) {

                    closeConn();
                    break;
                }
                if (msgFromClient.startsWith("/pr")) {

                    String[] arr = msgFromClient.split(" ", 3);
                    server.privateMessage(this, arr[1], this.nick + ": " + arr[2]);
                }
            } else {

                server.distributionToAll(msgFromClient, this);
            }

        }
    }

    private void closeConn() {
        server.unsubscribe(this);
        server.distributionToAll(nick + " покинул чат",this);
        try {
            this.dis.close();
            this.dos.close();
            this.socket.close();
        } catch (IOException ignored) {
        }
    }

    private void authorisation() throws IOException, SQLException, ClassNotFoundException {
        int i = 0;
        String clientMsg = "";
        while (true) {

            dos.writeUTF("Пройдите авторизацию: " +
                    "/auth login password");
            try {
                clientMsg = dis.readUTF();
            } catch (SocketException ignored) {
                System.out.println("Клиент не прошел авторизацию по таймауту");
                break;
            }


            if (clientMsg.startsWith("/auth")) {
                String[] words = clientMsg.split("\\s");
                String nickTry = server.getAuthService()
                        .getNickByLoginPassword(words[1], words[2]);
                if (nickTry != null) {

                    if (!server.isNickBusy(nickTry)) {
                        isAuthorized = true;
                        nick = nickTry;
                        sendMessage("/AuthOK " + this.nick);
                        server.distributionToAll("присоединился к чату",this);
                        server.subscribe(this);
                        break;
                    } else {
                        sendMessage("Пользователь " + this.nick + " уже авторизован!");
                    }

                } else {
                    dos.writeUTF("Клиент не зарегистрирован");
                }
            }else{
                dos.writeUTF("Неверный формат авторизации");
            }
            i++;
            if (i >= 3) {
                dos.writeUTF("Превышено кол-во попыток подключения");
                dos.writeUTF("Попробуйте через: " + limitSec + " секунд");

                try {
                    Thread.sleep(limitSec * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i = 0;
            }
        }

    }

    public synchronized void sendMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение: " + this.nick);
        }
    }
}
