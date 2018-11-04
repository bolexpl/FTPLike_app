package com.company.files;

/**
 * Klasa, która przechowuje informacje o transferze pliku
 */
public class TransferInfo {

    private String localPath;
    private String remotePath;
    private NewFile newFile;
    private boolean send;
    private int progress;

//    public TransferInfo(String localPath, String remotePath, boolean send) {
//        this.localPath = localPath;
//        this.remotePath = remotePath;
//        this.send = send;
//        this.progress = 0;
//    }

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

    boolean isSend() {
        return send;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
