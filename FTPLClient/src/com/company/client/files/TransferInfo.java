package com.company.client.files;

/**
 * Klasa, która przechowuje informacje o transferze pliku
 */
public class TransferInfo {

    private String localPath;
    private String remotePath;
    private NewFile newFile;
    private boolean send;
    private int progress;

    public TransferInfo(NewFile nf, boolean send) {
        this.localPath = nf.getLocalPath() + "/" + nf.getName();
        this.remotePath = nf.getRemotePath() + "/" + nf.getName();
        this.newFile = nf;
        this.send = send;
        this.progress = 0;
    }

    public NewFile getNewFile() {
        return newFile;
    }

    String getLocalPath() {
        return localPath;
    }

    String getRemotePath() {
        return remotePath;
    }

    public boolean isSend() {
        return send;
    }

    int getProgress() {
        return progress;
    }

    void setProgress(int progress) {
        this.progress = progress;
    }
}
