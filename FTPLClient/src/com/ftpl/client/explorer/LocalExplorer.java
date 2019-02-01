package com.ftpl.client.explorer;

import com.ftpl.client.files.FileInfo;
import com.ftpl.lib.UtilExplorerInterface;
import com.ftpl.lib.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Klasa do zarządzania plikami lokalnymi
 */
public class LocalExplorer implements IExplorer, Utils.AppendExplorer {

    private String dir;

    /**
     * Flaga widoczności ukrytych plików
     */
    private boolean showHidden;

    /**
     * @param dir początkowa ścieżka robocza
     */
    public LocalExplorer(String dir) {
        if (Utils.isAccess(dir)) {
            this.dir = dir;
        } else {
            this.dir = System.getProperty("user.home");
        }
        showHidden = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDir() {
        return dir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDir(String dir) {
        if (Utils.isAccess(dir)) {
            this.dir = dir;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(String path1, String path2) throws IOException {
        File f1 = new File(path1);
        File f2 = new File(path2);
        Files.copy(f1.toPath(), f2.toPath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pwd() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean touch(String name) throws IOException {
        File f = new File(dir + "/" + name);
        return f.createNewFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean append(String fileName, String data) throws IOException {
        //TODO do Utils
//        File f = new File(dir + "/" + fileName);
//
//        if (!f.exists() || !f.canWrite()) return false;
//
//        FileWriter writer = new FileWriter(f, true);
//        BufferedWriter buff = new BufferedWriter(writer);
//        PrintWriter printWriter = new PrintWriter(buff);
//
//        printWriter.write(data + "\n");
//
//        printWriter.close();
//        return true;
        return Utils.append(this, fileName, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invertHidden() {
        showHidden = !showHidden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(String path, String localPath) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String path, String localPath) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cd(String directory) {
        if (directory.equals("..")) {
            String s = new File(dir).getParent();
            if (s != null && Utils.isAccess(s)) {
                dir = s;
            }
        } else {

            String s = (dir.charAt(dir.length() - 1) != '/') ?
                    dir + "/" + directory : dir + directory;

            if (Utils.isAccess(s)) {
                dir = s;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean rm(String name) {
        return Utils.removeFile(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileInfo> listFiles() {
        final File path = new File(dir);
        File[] files;
        File[] dirs;
        if (showHidden) {
            files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory();
                }
            });

        } else {
            files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isHidden() && pathname.isDirectory();
                }
            });
            dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isHidden() && !pathname.isDirectory();
                }
            });
        }

        ArrayList<FileInfo> list = new ArrayList<>();
        if (files != null) {
            Arrays.sort(files);
            for (File f : files) {
                list.add(new FileInfo(f.getName(), true, f.isHidden(), f.length()));
            }
        }
        if (dirs != null) {
            Arrays.sort(dirs);
            for (File f : dirs) {
                list.add(new FileInfo(f.getName(), false, f.isHidden(), f.length()));
            }
        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean login(String login, String pass) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connectPassive() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connectActive() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mkdir(String dir) {
        File f = new File(this.dir + "/" + dir);
        return f.mkdir();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mv(String oldFile, String newFile) {
        File f = new File(oldFile);
        return f.renameTo(new File(newFile));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
    }
}
