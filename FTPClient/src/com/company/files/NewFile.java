package com.company.files;

/**
 * Klasa z informacjami do zapisywania nowego pliku
 * */
public class NewFile {

    private String name;
    private String remotePath;
    private String localPath;

    public NewFile(String name, String remotePath, String localPath) {
        this.name = name;
        this.remotePath = remotePath;
        this.localPath = localPath;
    }

    public String getName() {
        return name;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }
}
