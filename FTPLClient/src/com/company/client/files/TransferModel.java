package com.company.client.files;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

/**
 * Model dla tabeli transferów
 */
public class TransferModel extends AbstractTableModel {

    private List<TransferInfo> list;
    private final static String COLUMNS[] = {"Ścieżka lokalna", "Ścieżka zdalna", "Kierunek", "Postęp"};

    public TransferModel() {
        this.list = new ArrayList<>();

//        NewFile nf1 = new NewFile("asd", "qwe", "asf");
//        TransferInfo t1 = new TransferInfo(nf1, true);
//        NewFile nf2 = new NewFile("dfasfa", "qwfq", "ujm");
//        TransferInfo t2 = new TransferInfo(nf2, false);
//        NewFile nf3 = new NewFile("asebgebwd", "afawve", "yhn");
//        TransferInfo t3 = new TransferInfo(nf3, false);
//        list.add(t1);
//        list.add(t2);
//        list.add(t3);
    }

    /**
     * Metoda usuwająca obiekt modelu
     *
     * @param localPath  ścieżka lokalna
     * @param remotePath ścieżka zdalna
     */
    public void remove(String localPath, String remotePath) {
        for (TransferInfo ti : list) {
            if (ti.getLocalPath().equals(localPath)
                    && ti.getRemotePath().equals(remotePath)) {
                list.remove(ti);
                fireTableDataChanged();
                return;
            }
        }
    }

    /**
     * Metoda usuwająca obiekt modelu
     *
     * @param object obiekt do usunięcia
     */
    public void remove(TransferInfo object) {
        list.remove(object);
        fireTableDataChanged();
    }

    /**
     * Metoda dodająca obiekt do modelu
     *
     * @param ti obiekt reprezentujący transfer
     */
    public void addTransfer(TransferInfo ti) {
        list.add(ti);
        fireTableDataChanged();
    }

    public void setProgress(TransferInfo ti, int progress) {
        int i;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i) == ti) {
                break;
            }
        }
        ti.setProgress(progress);
        fireTableRowsUpdated(i, i);
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
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        TransferInfo ti = list.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return ti.getLocalPath();
            case 1:
                return ti.getRemotePath();
            case 2:
                if (ti.isSend())
                    return "wysyłanie  ------>";
                else
                    return "odbieranie <------";
            case 3:
                return ti.getProgress() + "%";
        }

        return ti;
    }
}
