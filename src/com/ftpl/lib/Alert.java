package com.ftpl.lib;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Klasa okna dialogowego do wyświetlania komunikatów
 * */
public class Alert extends JDialog {

    /**
     * @param x - komunikat
     */
    public Alert(String x) {
        JPanel contentPane = new JPanel();
        JLabel label1 = new JLabel(x);
        JButton buttonOK = new JButton("Ok");

        contentPane.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setBorder(new EmptyBorder(10,15,5,15));
        top.setLayout(new BorderLayout());
        top.add(label1,BorderLayout.WEST);

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout());
        bottom.add(buttonOK);

        contentPane.add(top,BorderLayout.NORTH);
        contentPane.add(bottom,BorderLayout.SOUTH);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        label1.setText(x);

        buttonOK.addActionListener(e -> dispose());

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = contentPane.getPreferredSize();
        d.width += 50;
        contentPane.setPreferredSize(d);
        pack();
        setLocation((int) screen.getWidth() / 2 - getWidth() / 2,
                (int) screen.getHeight() / 2 - getHeight() / 2);
        setVisible(true);
    }
}
