package com.ftpl.server.explorer;

import com.ftpl.lib.UtilExplorerInterface;
import com.ftpl.lib.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Klasa do zarządzania plikami lokalnymi
 */
public class LocalExplorer implements UtilExplorerInterface, Utils.AppendExplorer {

    private String dir;

    public LocalExplorer(String dir) {
        if (Utils.isAccess(dir)) {
            this.dir = dir;
        } else {
            this.dir = System.getProperty("user.home");
        }
    }

    /**
     * Metoda pobierająca aktualną ścieżkę z obiektu explorera
     *
     * @return ścieżka
     */
    public String getDir() {
        return dir;
    }

    /**
     * Metoda zmieniająca aktualną ścieżkę w obiekcie explorera
     *
     * @param dir Ścieżka do katalogu
     * @return sukces
     */
    public boolean setDir(String dir) {
        if (Utils.isAccess(dir)) {
            this.dir = dir;
            return true;
        }
        return false;
    }

    /**
     * Metoda do otwierania katalogu
     *
     * @param directory Nazwa katalogu
     * @return sukces
     */
    public boolean cd(String directory) {
        return Utils.cd(this, directory);
    }

    /**
     * Metoda do usuwania pliku
     *
     * @param name Nazwa pliku
     * @return sukces
     */
    public boolean rm(String name) {
        return Utils.removeFile(name);
    }

    /**
     * Metoda do przenoszenia i zmiany nazwy plików i katalogów
     *
     * @param oldFile Stara ścieżka z nazwą
     * @param newFile Nowa ścieżka z nazwą
     * @return sukces
     */
    public boolean mv(String oldFile, String newFile) {
        File f = new File(oldFile);
        return f.renameTo(new File(newFile));
    }

    /**
     * Metoda służąca do kopiowania plików między katalogami.
     * Metoda nie służy do pobierania i wysyłania plików.
     *
     * @param path1 Ścieżka źródłowa
     * @param path2 Ścieżka docelowa
     * @return sukces
     */
    public boolean copy(String path1, String path2) {

        File f1 = new File(path1);
        File f2 = new File(path2);

        try {
            Files.copy(f1.toPath(), f2.toPath());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Metoda służąca do przechodzenia do katalogu nadrzędnego
     *
     * @return sukces
     */
    public boolean cdParent() {
        String s = new File(dir).getParent();
        if (s != null && Utils.isAccess(s)) {
            dir = s;
        }
        return Utils.isAccess(s);
    }

    /**
     * Metoda do tworzenia katalogu
     *
     * @param dir Nazwa katalogu
     * @return sukces
     */
    public boolean mkdir(String dir) {
        File f = new File(this.dir + "/" + dir);
        return f.mkdir();
    }

    /**
     * Metoda tworząca pusty plik
     *
     * @param name Nazwa nowego pliku
     * @return sukces
     * @throws IOException wyjątek
     */
    public boolean touch(String name) throws IOException {
        File f = new File(dir + "/" + name);
        return f.createNewFile();
    }

    /**
     * Metoda dopisująca ciąg znaków do pliku
     *
     * @param fileName Nazwa pliku
     * @param data     Ciąg znaków
     * @return sukces
     * @throws IOException wyjątek
     */
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
     * Metoda listująca katalog roboczy
     *
     * @param showHidden czy pokazać ukryte
     * @return Lista elementów w katalogu
     */
    public List<File> listFiles(boolean showHidden) {
        final File path = new File(dir);
        File[] files;
        File[] dirs;
        if (showHidden) {
            files = path.listFiles(File::isDirectory);
            dirs = path.listFiles(pathname -> !pathname.isDirectory());
        } else {
            files = path.listFiles(pathname -> !pathname.isHidden() && pathname.isDirectory());
            dirs = path.listFiles(pathname -> !pathname.isHidden() && !pathname.isDirectory());
        }

        ArrayList<File> list = new ArrayList<>();
        if (files != null) {
            Arrays.sort(files);
            list.addAll(Arrays.asList(files));
        }
        if (dirs != null) {
            Arrays.sort(dirs);
            list.addAll(Arrays.asList(dirs));
        }

        return list;
    }
}
