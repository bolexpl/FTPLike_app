package com.company.explorer;

import com.company.files.FileInfo;

import java.io.IOException;
import java.util.List;

/**
 * Interfejs dla klas odpowiedzialnych za manipulacje plikami
 * */
public interface IExplorer {

    /**
     * Metoda pobierająca aktualną ścieżkę z obiektu explorera
     * */
    String getDir();

    /**
     * Metoda zmieniająca aktualną ścieżkę w obiekcie explorera
     * @param dir Ścieżka do katalogu
     * @see IOException
     * */
    boolean setDir(String dir) throws IOException;

    /**
     * Metoda służąca do kopiowania plików między katalogami.
     * Metoda nie służy do pobierania i wysyłania plików.
     * @param path1 Ścieżka źródłowa
     * @param path2 Ścieżka docelowa
     * @see IOException
     * */
    void copy(String path1, String path2) throws IOException;

    /**
     * Metoda aktualizująca ścieżkę katalogu roboczego
     * @see IOException
     * */
    void pwd() throws IOException;

    /**
     * Metoda tworząca pusty plik
     * @param name Nazwa nowego pliku
     * @return sukces
     * @see IOException
     * */
    boolean touch(String name) throws IOException;

    /**
     * Metoda dopisująca ciąg znaków do pliku
     * @param fileName Nazwa pliku
     * @param data Ciąg znaków
     * @return sukces
     * @see IOException
     * */
    boolean append(String fileName, String data) throws IOException;

    /**
     * Metoda negująca wartość atrybutu hidden
     * */
    void invertHidden();

    /**
     * Metoda do pobierania pliku z serwera
     * @param path Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     * */
    void get(String path, String localPath) ;

    /**
     * Metoda do wysyłania pliku na serwer
     * @param path Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     * */
    void put(String path, String localPath) ;

    /**
     * Metoda do otwierania katalogu
     * @param directory Nazwa katalogu
     * @see IOException
     * */
    void cd(String directory);

    /**
     * Metoda do usuwania pliku
     * @param name Nazwa pliku
     * @return sukces
     * @see IOException
     * */
    boolean rm(String name) throws IOException;

    /**
     * Metoda listująca katalog roboczy
     * @return Lista elementów w katalogu
     * */
    List<FileInfo> listFiles();

    /**
     * Metoda służąca do logowania na serwerze
     * @param login Login
     * @param pass Hasło
     * @return sukces
     * @see IOException
     * */
    boolean login(String login, String pass) throws IOException;

    /**
     * Metoda do połączenia w trybie pasywnym
     * @return sukces
     * @see IOException
     * */
    boolean connectPassive() throws IOException;

    /**
     * Metoda do połączenia w trybie aktywnym
     * @return sukces
     * @see IOException
     * */
    boolean connectActive() throws IOException;

    /**
     * Metoda do tworzenia katalogu
     * @param dir Nazwa katalogu
     * @return sukces
     * @see IOException
     * */
    boolean mkdir(String dir) throws IOException;

    /**
     * Metoda do przenoszenia i zmiany nazwy plików i katalogów
     * @param oldFile Stara ścieżka z nazwą
     * @param newFile Nowa ścieżka z nazwą
     * @return sukces
     * @see IOException
     * */
    boolean mv(String oldFile, String newFile) throws IOException;

    /**
     * Metoda kończąca połączenie
     * */
    void disconnect();
}
