package com.company.server;

/**
 * Klasa z informacjami potrzebnymi do zapisywania nowego pliku
 */
public class NewFile {
    public String path;

    public NewFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
