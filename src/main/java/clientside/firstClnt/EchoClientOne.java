package clientside.firstClnt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EchoClientOne extends JFrame {
    private final Integer PORT = 8088;
    private final String HOST = "localhost";
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    private JTextField messageField;
    private JTextArea chatArea;
    boolean isAuthorized;


    public EchoClientOne() {
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
                        isAuthorized = true;
                        chatArea.append(srvMsg + '\n');
                        break;
                    }
                    chatArea.append(srvMsg + '\n');
                }

                while (isAuthorized){
                    String srvMsg;
                    srvMsg = dis.readUTF();
                    chatArea.append(srvMsg + '\n');
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
        setTitle("WzzzzUp");
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
                super.windowClosing(e);
            }
        });

        setVisible(true);

    }

    private void send() throws IOException {//todo закончить логику

        if(socket.isClosed()){
            return;
        }

        if (messageField.getText() != null && !messageField.getText().trim().isEmpty()) {
            if(messageField.getText().equalsIgnoreCase("/end")){
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
            new EchoClientOne();
        });
    }
}
