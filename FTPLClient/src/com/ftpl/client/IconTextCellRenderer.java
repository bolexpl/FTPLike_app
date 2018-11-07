package com.ftpl.client;

import com.ftpl.client.files.FilesModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Klasa renderująca komórkę w tabeli z nazwą pliku i ikoną
 */
public class IconTextCellRenderer extends DefaultTableCellRenderer {

    private ImageIcon folderIcon;
    private ImageIcon fileIcon;

    public IconTextCellRenderer(ImageIcon folderIcon, ImageIcon fileIcon) {
        this.folderIcon = folderIcon;
        this.fileIcon = fileIcon;
    }

    /**
     * Returns the default table cell renderer.
     * <p>
     * During a printing operation, this method will be called with
     * <code>isSelected</code> and <code>hasFocus</code> values of
     * <code>false</code> to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * {@link JComponent#isPaintingForPrint()}.
     *
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at
     *                   <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     * @see JComponent#isPaintingForPrint()
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        FilesModel.FileCell f = (FilesModel.FileCell) value;

        if (f.isDirectory()) {
            setIcon(folderIcon);
        } else {
            setIcon(fileIcon);
        }

        setText(f.getName());

        return this;
    }
}
