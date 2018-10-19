package com.company.gui;

import com.company.clients.ClientThread;
import com.company.db.SQLiteJDBC;
import com.company.db.User;
import com.company.server.ServerThread;
import lib.Base64Coder;
import lib.Utils;

import java.io.Console;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CommandLine {

    private List<ClientThread> list;
    private boolean running;
    private Console console;

    public CommandLine() {
        running = false;
        list = new LinkedList<>();
        console = System.console();

        run();
    }

    private void run() {
        String s;
        while (true) {
            System.out.print(">");
            s = console.readLine();

            switch (s) {
                case "start":
                    if (!running)
                        startServer();
                    break;
                case "exit":
                case "quit":
                case "q":
                    if (running)
                        startServer();
                    else
                        System.exit(0);
                    break;
                case "con":

                    System.out.println("------------------");
                    for (ClientThread cl : list) {
                        System.out.println(cl.getName());
                    }
                    System.out.println("------------------");
                    break;
                case "db":
                    try {
                        System.out.println("------------------");
                        List<User> l = SQLiteJDBC.getInstance().selectAll();
                        for (User u : l) {
                            System.out.println(u.getLogin());
                        }
                        System.out.println("------------------");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "add":
                    System.out.print("Podaj login: ");
                    String login = console.readLine();

                    System.out.print("Podaj hasło: ");
                    String pass = new String(console.readPassword());

                    System.out.print("Potwierdź hasło: ");
                    String conf = new String(console.readPassword());

                    if (!pass.equals(conf)) {
                        System.out.println("Hasła się nie zgadzają.");
                        break;
                    }
                    try {
                        SQLiteJDBC db = SQLiteJDBC.getInstance();

                        if (db.select(login, null).size() != 0) {
                            System.out.println("Jest już taki login.");
                            break;
                        }

                        db.insert(login, Base64Coder.encodeString(pass));
                        System.out.println("Zarejestrowano.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "help":
                    System.out.println("start         - uruchomienie serwera");
                    System.out.println("con           - lista podłączonych klientów");
                    System.out.println("db            - lista użytkowników w bazie");
                    System.out.println("add           - zarejestrowanie użytkownika");
                    System.out.println("help          - wyświetlenie pomocy");
                    System.out.println("q, quit, exit - wyjście");
            }
        }
    }

    private void startServer() {
        if (running) {
            for (ClientThread cl : list) {
                cl.disconnect();
                list.remove(cl);
            }
            System.exit(0);
        } else {
            int port;
            do {
                System.out.print("Podaj numer portu: ");
                port = Integer.parseInt(console.readLine());
                if (Utils.isPortAvailable(port)) {
                    break;
                }
                System.out.println("Port niedostępny.");
            } while (true);


            ServerThread thread = new ServerThread(port, list);
            thread.start();

            try {
                Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                System.out.println("Dostępne interfejsy:");
                System.out.println("-------------");
                while (n.hasMoreElements()) {
                    NetworkInterface e = n.nextElement();
                    Enumeration<InetAddress> a = e.getInetAddresses();
                    String i = a.nextElement().toString().split("%")[1];
                    String ipv4 = a.nextElement().toString();

                    System.out.println(i);
                    System.out.println(ipv4);
                    System.out.println("-------------");
                }
                System.out.println("Serwer wystartował.");
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        running = !running;
    }
}
