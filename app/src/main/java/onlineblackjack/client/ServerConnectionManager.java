package onlineblackjack.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class ServerConnectionManager {
    private static final String HOST = "YOUR IP ADDRESS GOES HERE";
    private static final int PORT = 6479;
    private static Socket clientSocket;

    private static boolean isConnected = false;

    public static void connectToServer() throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(HOST, PORT));

        isConnected = clientSocket.isConnected();
        new Thread(new ReceiveData()).start();
    }

    public static boolean connected() {
        return isConnected;
    }

    public static Socket getClientSocket() {
        return clientSocket;
    }
}
