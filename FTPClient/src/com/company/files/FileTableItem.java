package com.company.files;

import java.text.DecimalFormat;

/**
 * Klasa przekazująca dane dla tabeli
 */
class FileTableItem {

    private String name;
    private String size;
    private boolean directory;

    FileTableItem(FileInfo f) {
        this.name = f.getName();
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

    FileTableItem(String name, String size, boolean directory) {
        this.name = name;
        this.size = size;
        this.directory = directory;
    }

    String getName() {
        return name;
    }

    String getSize() {
        return size;
    }

    boolean isDirectory() {
        return directory;
    }
}
