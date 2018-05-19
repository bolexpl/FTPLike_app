package com.company.explorer;

import com.company.Main;
import com.company.files.FileInfo;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Klasa do zarządzania plikami lokalnymi
 */
public class LocalExplorer implements IExplorer {

    private String dir;

    /**
     * Flaga widoczności ukrytych plików
     */
    private boolean showHidden;

    /**
     * @param dir początkowa ścieżka robocza
     */
    public LocalExplorer(String dir) {
        this.dir = dir;
        showHidden = false;
    }

    /**
     * Metoda pobierająca aktualną ścieżkę z obiektu explorera
     */
    @Override
    public String getDir() {
        return dir;
    }

    /**
     * Metoda zmieniająca aktualną ścieżkę w obiekcie explorera
     *
     * @param dir Ścieżka do katalogu
     */
    @Override
    public boolean setDir(String dir) {
        this.dir = dir;
        return Main.isAccess(dir);
    }

    /**
     * Metoda służąca do kopiowania plików między katalogami.
     * Metoda nie służy do pobierania i wysyłania plików.
     *
     * @param path1 Ścieżka źródłowa
     * @param path2 Ścieżka docelowa
     * @see IOException
     */
    @Override
    public void copy(String path1, String path2) throws IOException {
        File f1 = new File(path1);
        File f2 = new File(path2);
        Files.copy(f1.toPath(), f2.toPath());
    }

    /**
     * Metoda aktualizująca ścieżkę katalogu roboczego
     */
    @Override
    public void pwd() {

    }

    /**
     * Metoda tworząca pusty plik
     *
     * @param name Nazwa nowego pliku
     * @return sukces
     * @see IOException
     */
    @Override
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
     * @see IOException
     */
    @Override
    public boolean append(String fileName, String data) throws IOException {
        File f = new File(dir + "/" + fileName);

        if (!f.exists() || !f.canWrite()) return false;

        FileWriter writer = new FileWriter(f, true);
        BufferedWriter buff = new BufferedWriter(writer);
        PrintWriter printWriter = new PrintWriter(buff);

        printWriter.write(data + "\n");

        printWriter.close();
        return true;
    }

    /**
     * Metoda negująca wartość atrybutu hidden
     */
    @Override
    public void invertHidden() {
        showHidden = !showHidden;
    }

    /**
     * Metoda do pobierania pliku z serwera
     *
     * @param path      Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     */
    @Override
    public void get(String path, String localPath) {

    }

    /**
     * Metoda do wysyłania pliku na serwer
     *
     * @param path      Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     */
    @Override
    public void put(String path, String localPath) {

    }

    /**
     * Metoda do otwierania katalogu
     *
     * @param directory Nazwa katalogu
     */
    @Override
    public void cd(String directory) {
        if (directory.equals(".."))
            dir = (new File(dir)).getParent();
        else {
            if (dir.charAt(dir.length() - 1) != '/')
                dir += "/";
            dir += directory;
        }
    }

    /**
     * Metoda do usuwania pliku
     *
     * @param name Nazwa pliku
     * @return sukces
     */
    @Override
    public boolean rm(String name) {
        File f = new File(name);

        if (!f.exists()) return false;

        if (f.isDirectory()) {
            String[] entries = f.list();
            if (entries != null){
                for (String s : entries) {
                    rm(name+"/"+s);
                }
            }
        }

        return f.delete();
    }

    /**
     * Metoda listująca katalog roboczy
     *
     * @return Lista elementów w katalogu
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
     * Metoda służąca do logowania na serwerze
     *
     * @param login Login
     * @param pass  Hasło
     * @return sukces
     */
    @Override
    public boolean login(String login, String pass) {
        return false;
    }

    /**
     * Metoda do połączenia w trybie pasywnym
     *
     * @return sukces
     */
    @Override
    public boolean connectPassive() {
        return false;
    }

    /**
     * Metoda do połączenia w trybie aktywnym
     *
     * @return sukces
     */
    @Override
    public boolean connectActive() {
        return false;
    }

    /**
     * Metoda do tworzenia katalogu
     *
     * @param dir Nazwa katalogu
     * @return sukces
     */
    @Override
    public boolean mkdir(String dir) {
        File f = new File(this.dir + "/" + dir);
        return f.mkdir();
    }

    /**
     * Metoda do przenoszenia i zmiany nazwy plików i katalogów
     *
     * @param oldFile Stara ścieżka z nazwą
     * @param newFile Nowa ścieżka z nazwą
     * @return sukces
     */
    @Override
    public boolean mv(String oldFile, String newFile) {
        File f = new File(oldFile);
        return f.renameTo(new File(newFile));
    }

    /**
     * Metoda kończąca połączenie
     */
    @Override
    public void disconnect() {
    }
}
