package treeChat.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Packet implements Serializable {
    private Socket socket;
    private Message message;

    public Packet(Socket socket, Message message) {
        this.message = message;
        this.socket = socket;
    }
    public Socket getSocket() {
        return socket;
    }
    public Message getMessage() {
        return message;
    }
}
