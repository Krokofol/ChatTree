package treeChat.threads;

import treeChat.node.Node;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread implements Runnable {
    private ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Node.acceptConnecting(serverSocket.accept());
            } catch (IOException e) {
                System.out.println("Error : creating socket\naccepting connection");
                e.printStackTrace();
                System.exit(4);
            }
        }
    }
}
