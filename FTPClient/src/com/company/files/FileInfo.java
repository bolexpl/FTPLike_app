package com.company.files;

/**
 * Klasa zawierająca informacje o pliku potrzebne dla modelu tabeli
 */
public class FileInfo {

    /**
     * Nazwa pliku
     */
    private String name;

    /**
     * Czy katalog
     */
    private boolean directory;

    /**
     * Czy jest ukryty
     */
    private boolean hidden;

    /**
     * Rozmiar pliku w bajtach
     */
    private long length;

    public FileInfo(String name, boolean directory, boolean hidden, long length) {
        this.name = name;
        this.directory = directory;
        this.hidden = hidden;
        this.length = length;
    }

    /**
     * {@link FileInfo#name}
     */
    String getName() {
        return name;
    }

    /**
     * {@link FileInfo#directory}
     */
    boolean isDirectory() {
        return directory;
    }

    /**
     * {@link FileInfo#hidden}
     */
    boolean isHidden() {
        return hidden;
    }

    /**
     * {@link FileInfo#length}
     */
    long getLength() {
        return length;
    }
}
