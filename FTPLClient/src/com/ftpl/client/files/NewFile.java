package com.ftpl.client.files;

/**
 * Klasa z informacjami do zapisywania nowego pliku
 */
public class NewFile {

    private String name;
    private String remotePath;
    private String localPath;
    private TransferInfo ti;

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

    public TransferInfo getTi() {
        return ti;
    }

    public void setTi(TransferInfo ti) {
        this.ti = ti;
    }
}
