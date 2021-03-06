package com.ftpl.server.gui;

import com.ftpl.lib.Utils;
import com.ftpl.server.server.ServerThread;
import com.ftpl.server.clients.ClientsModel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

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
        URL url = getClass().getResource("/res/ftpl_server.png");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = toolkit.createImage(url);
        setIconImage(img);

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
        getRootPane().setDefaultButton(start);
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
            ServerThread thread = new ServerThread(port, this, model, model.getList());
            thread.start();
            start.setText("Zakończ");
            addColoredText("Available addresses:", Color.BLACK);

            try {
                Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                while (n.hasMoreElements()) {
                    NetworkInterface networkInterface = n.nextElement();
                    List<String> ips = Utils.nextInterface(networkInterface);

                    if (ips == null || ips.size() == 0) continue;

                    addColoredText(networkInterface.getName(), Color.BLACK);

                    for(String s : ips){
                        addColoredText(s, Color.BLACK);
                        addColoredText("-------------", Color.BLACK);
                    }
                }
                addColoredText("Server started at port " + port, Color.BLACK);

            } catch (SocketException e) {
                e.printStackTrace();
            }
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
}
