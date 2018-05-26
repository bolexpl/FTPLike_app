package com.company.files;

import com.company.explorer.IExplorer;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * Klasa modelu dla danych w tabeli
 */
public class FilesModel extends AbstractTableModel {

    private final static String[] COLUMNS = {"Nazwa", "Rozmiar", "Typ"};
    private Vector<FileTableItem> files;
    private IExplorer explorer;

    public FilesModel() {
        super();
    }

    public FilesModel(IExplorer explorer) {
        super();
        this.explorer = explorer;
        updateData(explorer.listFiles());
    }

    /**
     * Metoda ustawiająca explorer
     *
     * @param explorer Explorer
     */
    public void setExplorer(IExplorer explorer) {
        this.explorer = explorer;
        updateData(explorer.listFiles());
    }

    /**
     * Metoda aktualizująca dane w modelu
     */
    public void updateData() {
        if (explorer != null)
            updateData(explorer.listFiles());
    }

    /**
     * Metoda aktualizująca dane w modelu
     *
     * @param list Lista plików
     */
    public void updateData(List<FileInfo> list) {
        if (list == null) {
            files.clear();
            return;
        }

        Vector<FileTableItem> tmp = new Vector<>();

        tmp.add(new FileTableItem("..", null, true));

        for (FileInfo f : list) {
            tmp.add(new FileTableItem(f));
        }
        files = tmp;
    }

    /**
     * Returns the number of rows in the explorer. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the explorer
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        if (files == null) return 0;
        return files.size();
    }

    /**
     * Returns the number of columns in the explorer. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the explorer
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
     * Returns false.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
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

        if (files == null) return null;

        FileTableItem f = files.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return new FileCell(f.getName(), f.isDirectory());
            case 1:
                return !f.isDirectory() ? f.getSize() : null;
            case 2:
                if (f.isDirectory()) {
                    return "katalog";
                } else {
                    return "plik";
                }
            default:
                return f;
        }
    }

    /**
     * Klasa zawierająca podstwowe informacje o pliku
     */
    public class FileCell {

        /**
         * Nazwa pliku
         */
        private String name;

        /**
         * Czy katalog
         */
        private boolean directory;

        FileCell(String name, boolean directory) {
            this.name = name;
            this.directory = directory;
        }

        /**
         * {@link FileCell#name}
         */
        public String getName() {
            return name;
        }

        /**
         * {@link FileCell#directory}
         */
        public boolean isDirectory() {
            return directory;
        }
    }
}
