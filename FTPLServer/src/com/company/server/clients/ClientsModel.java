package com.company.server.clients;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * Model klientów dla tabeli
 */
public class ClientsModel extends AbstractTableModel {

    private List<ClientThread> list;
    private static final String COLUMNS[] = {"Login", "IP"};

    public ClientsModel() {
        this.list = new LinkedList<>();
    }

    /**
     * Metoda kończy wszystkie połączenia
     */
    public void closeAll() {
        for (ClientThread cl : list) {
            cl.disconnect();
            list.remove(cl);
        }
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return list.size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClientThread cl = list.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cl.getLogin();
            case 1:
                return cl.getControlSocket().getInetAddress().getHostAddress();
        }
        return null;
    }

    public List<ClientThread> getList() {
        return list;
    }
}
