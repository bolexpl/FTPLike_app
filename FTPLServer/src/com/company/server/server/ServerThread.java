package com.company.server.server;

import com.company.server.clients.ClientThread;
import com.company.server.clients.ClientsModel;
import com.company.server.gui.MainWindow;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

/**
 * Klasa wątku serwerowego
 */
public class ServerThread extends Thread {

    private int port;
    private MainWindow window;
    private ClientsModel model;
    private List<ClientThread> list;

    public ServerThread(int port, MainWindow window, ClientsModel model, List<ClientThread> list) {
        this.port = port;
        this.window = window;
        this.model = model;
        this.list = list;
    }

    public ServerThread(int port, List<ClientThread> list) {
        this.port = port;
        this.list = list;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                ClientThread clientThread =
                        new ClientThread(serverSocket.accept(), window, model, list);

                if (window != null) {
                    window.addColoredText("Connection from " + clientThread
                            .getControlSocket()
                            .getInetAddress()
                            .getHostAddress(), Color.GREEN);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
