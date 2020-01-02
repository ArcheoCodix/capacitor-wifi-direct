package fr.archeocodix.wifidirect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread {
    Socket socket;
    String hostAdd;

    Client(InetAddress hostAddress) {
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
