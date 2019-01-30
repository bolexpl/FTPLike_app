package com.ftpl.client.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Klasa okna dialogowego do wprowadzania nazwy pliku lub katalogu
 */
class Prompt extends JDialog {

    private JTextField nameField;
    private JLabel error;
    private MainWindow mainWindow;

    /**
     * @param mainWindow Referencja do głównego okna
     * */
    Prompt(MainWindow mainWindow) {
        this(mainWindow, null);
    }

    /**
     * @param mainWindow Referencja do głównego okna
     * @param value wartość w polu edycyjnym
     * */
    Prompt(MainWindow mainWindow, String value) {
        this.mainWindow = mainWindow;
        nameField = new JTextField();
        if(value != null){
            nameField.setText(value);
        }
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        GridLayout grid = new GridLayout(2, 1);
        GridLayout grid2 = new GridLayout(1, 2);
        grid2.setHgap(10);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        JPanel right = new JPanel();
        right.setLayout(grid2);
        JButton buttonOK = new JButton("Ok");
        JButton buttonCancel = new JButton("Anuluj");
        right.add(buttonOK);
        right.add(buttonCancel);
        right.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 10));

        JPanel top = new JPanel();


        top.setLayout(grid);
        top.setBorder(new EmptyBorder(15, 15, 0, 15));
        top.add(new JLabel("Podaj nazwę:"));
        error = new JLabel();

        top.add(nameField);

        top.add(error);
        bottom.add(right, BorderLayout.EAST);
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(bottom, BorderLayout.SOUTH);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        error.setForeground(Color.RED);

        buttonOK.addActionListener(e -> {
            error.setText("");
            onOK();
        });

        buttonCancel.addActionListener(e -> dispose());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) screen.getWidth() / 2 - getWidth() / 2,
                (int) screen.getHeight() / 2 - getHeight() / 2);
        setVisible(true);
    }

    /**
     * Metoda wywałana na przycisk OK
     */
    private void onOK() {
        String s = nameField.getText();
        if (!s.equals("")) {
            mainWindow.setTmpName(s);
            dispose();
        }else{
            error.setText("Pole nie może być puste");
        }
    }
}
