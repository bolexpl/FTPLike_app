package com.company.server.gui;

import com.company.server.db.SQLiteJDBC;
import lib.Alert;
import lib.Base64Coder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Okno dialogowe do rejestracji użytkownika
 */
class RegisterDialog extends JDialog {

    private JTextField loginField;
    private JPasswordField passwordField;

    RegisterDialog() {
        JPanel mainPanel = new JPanel();
        JPanel center = new JPanel();
        JPanel bottom = new JPanel();
        center.setLayout(new GridLayout(2, 1));

        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton buttonOK = new JButton("Rejestruj");
        JButton buttonCancel = new JButton("Anuluj");
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOk());
        buttonCancel.addActionListener(e -> dispose());

        mainPanel.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        buttonOK.addActionListener(actionEvent -> dispose());

        bottom.add(buttonOK);
        bottom.add(buttonCancel);

        //<center>
        loginField = new JTextField(30);
        passwordField = new JPasswordField(30);
        JPanel loginPanel = new JPanel();
        JPanel passwordPanel = new JPanel();

        loginPanel.add(new JLabel("Login:"));
        loginPanel.add(loginField);
        passwordPanel.add(new JLabel("Hasło:"));
        passwordPanel.add(passwordField);

        center.add(loginPanel);
        center.add(passwordPanel);
        //</center>

        mainPanel.add(center);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize().getSize();
        pack();
        setLocation((int) screen.getWidth() / 2 - getWidth() / 2,
                (int) screen.getHeight() / 2 - getHeight() / 2);
        setVisible(true);
    }

    /**
     * Metoda wywołana przy wciśnięciu przycisku 'Rejestruj'
     */
    private void onOk() {
        String login = loginField.getText();
        if (login.equals("")) {
            new Alert("Login jest wymagany");
            return;
        }

        char[] pass = passwordField.getPassword();
        String s = new String(pass);
        if (s.equals("")) {
            new Alert("Hasło jest wymagane");
            return;
        }

        SQLiteJDBC db = SQLiteJDBC.getInstance();
        try {

            if (db.select(login, null).size() != 0) {
                new Alert("Istnieje już taki login");
                return;
            }

            db.insert(login, Base64Coder.encodeString(s));
        } catch (SQLException e) {
            new Alert("Nie udało się zarejestrować");
        }
    }
}
