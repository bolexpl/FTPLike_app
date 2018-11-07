package com.company.client.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Klasa okna dialogowego do wprowadzenia ciągu znaków do zapisania do pliku
 */
class LinePrompt extends JDialog {

    private JTextField nameField;
    private MainWindow mainWindow;

    /**
     * @param mainWindow Referencja do głównego okna
     */
    LinePrompt(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        nameField = new JTextField(40);
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
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


        top.setBorder(new EmptyBorder(15, 15, 0, 15));

        top.add(nameField);

        bottom.add(right, BorderLayout.EAST);
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(bottom, BorderLayout.SOUTH);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

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
        mainWindow.setTmpName(s);
        dispose();
    }
}
