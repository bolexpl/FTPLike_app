package com.company.gui;

import com.company.clients.ClientThread;
import com.company.clients.ClientsModel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Klasa głównego okna
 */
public class MainWindow extends JFrame {

    private JTextPane textPane;
    private JTextField portField;
    private JScrollBar scroll;
    private ClientsModel model;
    private JButton start;
    private boolean running = false;

    public MainWindow() {
        super("FTP-like Server");

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        createMenuBar();

        JPanel top = prepareTop();

        textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        textPane.setPreferredSize(new Dimension(500, 200));
        scroll = scrollPane.getVerticalScrollBar();

        model = new ClientsModel();
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(500, 150));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(500, 150));


        add(contentPane);
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(scrollPane);
        contentPane.add(tableScroll, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Metoda przygotowująca górny panel
     */
    private JPanel prepareTop() {
        JPanel top = new JPanel();
        portField = new JTextField(4);
        portField.setText("3000");
        start = new JButton("Uruchom");
        start.addActionListener(e -> serverStart());
        top.add(new JLabel("Port: "));
        top.add(portField);
        top.add(start);
        return top;
    }

    /**
     * Metoda przygotowuje paek Menu
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu users = new JMenu("Użytkownicy");
        users.setMnemonic(KeyEvent.VK_U);
        JMenuItem item;
        item = new JMenuItem("Lista");
        item.setMnemonic(KeyEvent.VK_L);
        item.setToolTipText("Lista zarejestrowanych użytkowników");
        item.addActionListener(e -> new UsersListDialog());
        users.add(item);

        item = new JMenuItem("Rejestracja");
        item.setMnemonic(KeyEvent.VK_R);
        item.setToolTipText("Rejestracja nowego użytkownika");
        item.addActionListener(e -> new RegisterDialog());
        users.add(item);

        menuBar.add(users);
        setJMenuBar(menuBar);
    }

    /**
     * Metoda uruchamiająca wątek serwera
     */
    private void serverStart() {
        if (running) {
            model.closeAll();
            System.exit(0);
        } else {
            portField.setEnabled(false);
            int port = Integer.parseInt(portField.getText());
            ServerThread thread = new ServerThread(port, this);
            thread.start();
            start.setText("Zakończ");
            addColoredText("Server started at port " + Integer.toString(port), Color.BLACK);
        }
        running = !running;
    }

    /**
     * Metoda wypisująca komunikaty w głównym oknie
     *
     * @param s     Komunikat
     * @param color Kolor
     */
    public void addColoredText(String s, Color color) {
        StyledDocument doc = textPane.getStyledDocument();

        Style style = textPane.addStyle("Color Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), s + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        scroll.setValue(scroll.getMaximum());
    }

    /**
     * Klasa wątku serwerowego
     */
    class ServerThread extends Thread {

        private int port;
        MainWindow window;

        ServerThread(int port, MainWindow window) {
            this.port = port;
            this.window = window;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    ClientThread clientThread =
                            new ClientThread(serverSocket.accept(), window, model);

                    addColoredText("Connection from " + clientThread
                            .getControlSocket()
                            .getInetAddress()
                            .getHostAddress(), Color.GREEN);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
