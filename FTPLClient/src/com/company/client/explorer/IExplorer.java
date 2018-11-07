package com.company.client.explorer;

import com.company.client.files.FileInfo;

import java.io.IOException;
import java.util.List;

/**
 * Interfejs dla klas odpowiedzialnych za manipulacje plikami
 */
public interface IExplorer {

    /**
     * Metoda pobierająca aktualną ścieżkę z obiektu explorera
     *
     * @return ścieżka
     */
    String getDir();

    /**
     * Metoda zmieniająca aktualną ścieżkę w obiekcie explorera
     *
     * @param dir Ścieżka do katalogu
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean setDir(String dir) throws IOException;

    /**
     * Metoda służąca do kopiowania plików między katalogami.
     * Metoda nie służy do pobierania i wysyłania plików.
     *
     * @param path1 Ścieżka źródłowa
     * @param path2 Ścieżka docelowa
     * @throws IOException wyjątek
     */
    void copy(String path1, String path2) throws IOException;

    /**
     * Metoda aktualizująca ścieżkę katalogu roboczego
     *
     * @throws IOException wyjątek
     */
    void pwd() throws IOException;

    /**
     * Metoda tworząca pusty plik
     *
     * @param name Nazwa nowego pliku
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean touch(String name) throws IOException;

    /**
     * Metoda dopisująca ciąg znaków do pliku
     *
     * @param fileName Nazwa pliku
     * @param data     Ciąg znaków
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean append(String fileName, String data) throws IOException;

    /**
     * Metoda negująca wartość atrybutu hidden
     */
    void invertHidden();

    /**
     * Metoda do pobierania pliku z serwera
     *
     * @param path      Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     */
    void get(String path, String localPath);

    /**
     * Metoda do wysyłania pliku na serwer
     *
     * @param path      Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     */
    void put(String path, String localPath);

    /**
     * Metoda do otwierania katalogu
     *
     * @param directory Nazwa katalogu
     */
    void cd(String directory);

    /**
     * Metoda do usuwania pliku
     *
     * @param name Nazwa pliku
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean rm(String name) throws IOException;

    /**
     * Metoda listująca katalog roboczy
     *
     * @return Lista elementów w katalogu
     */
    List<FileInfo> listFiles();

    /**
     * Metoda służąca do logowania na serwerze
     *
     * @param login Login
     * @param pass  Hasło
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean login(String login, String pass) throws IOException;

    /**
     * Metoda do połączenia w trybie pasywnym
     *
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean connectPassive() throws IOException;

    /**
     * Metoda do połączenia w trybie aktywnym
     *
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean connectActive() throws IOException;

    /**
     * Metoda do tworzenia katalogu
     *
     * @param dir Nazwa katalogu
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean mkdir(String dir) throws IOException;

    /**
     * Metoda do przenoszenia i zmiany nazwy plików i katalogów
     *
     * @param oldFile Stara ścieżka z nazwą
     * @param newFile Nowa ścieżka z nazwą
     * @return sukces
     * @throws IOException wyjątek
     */
    boolean mv(String oldFile, String newFile) throws IOException;

    /**
     * Metoda kończąca połączenie
     */
    void disconnect();
}
