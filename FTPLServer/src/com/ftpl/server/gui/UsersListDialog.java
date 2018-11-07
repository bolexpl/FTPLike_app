package com.ftpl.server.gui;

import com.ftpl.server.db.SQLiteJDBC;
import com.ftpl.server.db.User;
import com.ftpl.server.db.UsersModel;
import com.ftpl.lib.Alert;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.sql.SQLException;

/**
 * Okno dialogowe z listą użytkowników
 * */
class UsersListDialog extends JDialog {

    private JTable table;
    private UsersModel model;

    UsersListDialog() {
        JPanel mainPanel = new JPanel();
        JPanel center = new JPanel();
        JPanel bottom = new JPanel();

        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton buttonOK = new JButton("Ok");
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> dispose());

        mainPanel.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        buttonOK.addActionListener(actionEvent -> dispose());

        try {
            List<User> list = SQLiteJDBC.getInstance().selectAll();

            model = new UsersModel(list);
            table = new JTable(model);

            table.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //PPM
                    if (e.getButton() == 3) {

                        int r = table.rowAtPoint(e.getPoint());

                        if (table.getSelectedRows().length < 2
                                && r >= 0
                                && r < table.getRowCount()) {
                            table.setRowSelectionInterval(r, r);
                        }

                        PopUp popUp = new PopUp();
                        popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(JLabel.CENTER);
            table.setDefaultRenderer(String.class, renderer);
            table.setDefaultRenderer(Integer.class, renderer);

            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            center.add(scrollPane);

            bottom.add(buttonOK);
        } catch (SQLException e) {
            new Alert("Błąd wczytania użytkowników");
            dispose();
        }

        mainPanel.add(center);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize().getSize();
        setSize(400, 300);
        setLocation((int) screen.getWidth() / 2 - getWidth() / 2,
                (int) screen.getHeight() / 2 - getHeight() / 2);
        setVisible(true);
    }

    /**
     * Metoda do usunięcia użytkownika
     * */
    private void delUser() {
        int[] indexes = table.getSelectedRows();

        SQLiteJDBC db = SQLiteJDBC.getInstance();
        try {
            for (int i : indexes) {
                int x = (Integer) table.getValueAt(i, 0);
                db.delete(x);
            }
            model.refresh();
            model.fireTableDataChanged();
        } catch (SQLException e) {
            new Alert("Nie udało się usunąć");
        }
    }

    /**
     * Klasa menu kontekstowego
     */
    class PopUp extends JPopupMenu implements ActionListener {

        JMenuItem delete;

        PopUp() {
            delete = new JMenuItem("Usuń");
            delete.addActionListener(this);
            add(delete);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            switch (actionCommand) {
                case "Usuń":

                    delUser();

                    break;
            }
        }

    }
}
