package com.company.files;

/**
 * Klasa, która przechowuje informacje o transferze pliku
 */
public class TransferInfo {

    private String localPath;
    private String remotePath;
    private boolean send;

    public TransferInfo(String localPath, String remotePath, boolean send) {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.send = send;
    }

    String getLocalPath() {
        return localPath;
    }

    String getRemotePath() {
        return remotePath;
    }

    boolean isSend() {
        return send;
    }
}
