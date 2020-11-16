package treeChat.threads;

import treeChat.message.Packet;
import treeChat.node.Node;

import java.net.Socket;

public class CheсkingThread implements Runnable{

    public void run(){
        while (true) {
            sleep();
            checkTime();
            cleanMessages();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkTime() {
        int i;
        int j;
        i = 0;
        for (Long t : Node.time.values()) {
            if (System.currentTimeMillis() - t > 7000) {
                j = 0;
                for (Socket socket : Node.time.keySet()) {
                    if (i == j) {
                        connectToNewHost(socket);
                        Node.sockets.remove(socket);
                        Node.time.remove(socket, t);
                        break;
                    }
                    j++;
                }
            }
            i++;
        }
    }

    private void connectToNewHost(Socket socket) {
        if (Node.sockets.peek().getInetAddress().toString().equals(socket.getInetAddress().toString()) && Node.sockets.peek().getPort() == socket.getPort()) {
            Node.sockets.poll();
            if (Node.nextIP != null) {
                System.out.println("Connecting to : " + String.copyValueOf(Node.nextIP.toCharArray(), 1, Node.nextIP.length() - 1) + ":" + Node.nextPort);
                Node.connecting(String.copyValueOf(Node.nextIP.toCharArray(), 1, Node.nextIP.length() - 1), Node.nextPort);
            }
        }
    }

    private void cleanMessages() {
        if (Node.packets.size() > 200)
            for (int ii = 0; ii < 50; ii++) {
                Node.packets.poll();
            }
        if (Node.receivedPackets.size() > 200)
            for (int ii = 0; ii < 50; ii++) {
                Node.receivedPackets.poll();
            }
    }
}
