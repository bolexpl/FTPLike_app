package com.company.files;

import java.text.DecimalFormat;

/**
 * Klasa przekazująca dane dla tabeli
 * */
public class FileTableItem {

    private String name;
    private boolean hidden;
    private String size;
    private boolean directory;

    public FileTableItem(FileInfo f) {
        this.name = f.getName();
        this.hidden = f.isHidden();
        this.directory = f.isDirectory();

        long length = f.getLength();
        if (length < 1000) {
            size = Long.toString(length) + " B";
            return;
        }

        DecimalFormat df = new DecimalFormat(".##");

        double length2 = (double) length / 1000;
        if (length2 < 1000) {
            size = df.format(length2) + " KB";
            return;
        }

        length2 /= 1000;
        if (length2 < 1000) {
            size = df.format(length2) + " MB";
            return;
        }

        length2 /= 1000;
        if (length2 < 1000) {
            size = df.format(length2) + " GB";
        }
    }

    public FileTableItem(String name, boolean hidden, String size, boolean directory) {
        this.name = name;
        this.hidden = hidden;
        this.size = size;
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public boolean isDirectory() {
        return directory;
    }
}
