package clientside.secondClnt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class EchoClientTwo extends JFrame {
    private final Integer PORT = 8088;
    private final String HOST = "localhost";
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    private JTextField messageField;
    private JTextArea chatArea;
    boolean isAuthorized;
    private String nick;
    private String histFilePath = ".\\src\\main\\java\\UserPrHistory\\";


    public EchoClientTwo() {
        chatArea = new JTextArea();
        try {
            connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createGUI();


    }

    private void connectToServer() throws IOException {
        socket = new Socket(HOST, PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        isAuthorized = false;


        new Thread(() -> {
            try {
                while (!isAuthorized) {
                    String srvMsg;
                    srvMsg = dis.readUTF();

                    if (srvMsg.startsWith("/AuthOK")) {
                        nick = srvMsg.split(" ",2)[1];
                        setTitle(nick);

                        isAuthorized = true;
                        chatArea.append(srvMsg + '\n');
                        saveHistory(srvMsg);
                        break;
                    }
                    chatArea.append(srvMsg + '\n');
                }

                while (isAuthorized){

                    String srvMsg;
                    srvMsg = dis.readUTF();
                    chatArea.append(srvMsg + '\n');
                    saveHistory(srvMsg);
                    send();

                }
            } catch (IOException ignored) {

            }
        }).start();
    }

    private void closeConn() throws IOException {
        //this.socket.close();
        this.dis.close();
        this.dos.close();
        this.socket.close();
    }


    private void createGUI(){
        setBounds(400, 400, 400, 400);
        setTitle("WhazzUppp");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel BikiniBottom = new JPanel(new BorderLayout());
        messageField = new JTextField();
        BikiniBottom.add(messageField, BorderLayout.CENTER);
        JButton sendButton = new JButton("Отправить");
        BikiniBottom.add(sendButton, BorderLayout.EAST);
        add(BikiniBottom, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            try {
                send();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        messageField.addActionListener(e -> {
            try {
                send();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    closeConn();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                super.windowClosing(e);
            }
        });

        setVisible(true);

    }

    private void saveHistory(String msg) throws IOException {
        File histFile = new File(histFilePath+this.nick+".txt");
        if(!histFile.exists()){
            histFile.createNewFile();
        }
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(histFile,true))){
            bw.write(msg+'\n');
        }

    }

    private void send() throws IOException {

        if (socket.isClosed()) {
            return;
        }

        if(messageField.getText().startsWith("/newLogin")){
            nick = messageField.getText().split(" ",2)[1];
            setTitle(nick);
        }

        if (messageField.getText() != null && !messageField.getText().trim().isEmpty()) {
            if (messageField.getText().equalsIgnoreCase("/end")) {
                dos.writeUTF("/end");
                isAuthorized = false;
                try {
                    closeConn();
                    messageField.setText("");
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                dos.writeUTF(messageField.getText());
                messageField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EchoClientTwo();
        });
    }
}
