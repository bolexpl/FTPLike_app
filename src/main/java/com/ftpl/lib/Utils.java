package com.ftpl.lib;

import java.io.*;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

/**
 * Klasa konwersji między typem long i talbicą bajtów
 */
public class Utils {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static String path = System.getProperty("user.home");
    public static boolean debug = false;

    /**
     * Metoda zamienia long na tablicę bajtów
     *
     * @param l liczba typu long
     * @return tablica bajtów
     */
    public static byte[] longToByte(long l) {
        buffer.clear();
        buffer.putLong(0, l);
        return buffer.array();
    }

    /**
     * Metoda zamieniająca tablicę bajtów na typ long
     *
     * @param bytes tablica bajtów
     * @return liczba typu long
     */
    public static long byteToLong(byte[] bytes) {
        buffer.clear();
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * Metoda sprawdzająca czy port jest dostępny do użycia.
     *
     * @param port Numer portu
     * @return Czy dostępny port
     */
    public static boolean isPortAvailable(int port) {

        if (port < Protocol.MIN_PORT_NUMBER || port > Protocol.MAX_PORT_NUMBER) {
            return false;
        }

        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Metoda sprawdzająca czy jest dostęp do katalogu.
     *
     * @param dir Ścieżka do katalogu
     * @return Czy dostępny katalog
     */
    public static boolean isAccess(String dir) {
        File f = new File(dir);
        return f.exists() && f.isDirectory() && f.canRead() && f.canExecute();
    }

    /**
     * Metoda sprawdzająca równość tablic o różnych długościach
     *
     * @param bigger  większa tablica
     * @param smaller mniejsza tablica
     * @return Czy wartości są równe
     */
    public static boolean equalsArrays(byte[] bigger, byte[] smaller) {

        for (int i = 0; i < smaller.length; i++)
            if (bigger[i] != smaller[i])
                return false;

        return true;
    }

    /**
     * Zmiana katalogu roboczego
     *
     * @param explorer  explorer
     * @param directory katalog
     * @return czy powodzenie
     */
    public static boolean cd(UtilExplorerInterface explorer, String directory) {
        String dir = explorer.getDir();
        String s = (dir.charAt(dir.length() - 1) != '/') ?
                dir + "/" + directory : dir + directory;

        try {
            explorer.setDir(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Utils.isAccess(s);
    }

    /**
     * Usuwanie pliku
     *
     * @param name nazwa pliku
     * @return czy powodzenie
     */
    public static boolean removeFile(String name) {
        File f = new File(name);

        if (!f.exists()) return false;

        if (f.isDirectory()) {
            String[] entries = f.list();
            if (entries != null) {
                for (String s : entries) {
                    removeFile(name + "/" + s);
                }
            }
        }

        return f.delete();
    }

    /**
     * Dopisywanie do pliku
     * @param explorer explorer
     * @param fileName nazwa pliku
     * @param data dane
     * @return sukces
     * @throws IOException gdy błąd I/O
     */
    public static boolean append(AppendExplorer explorer, String fileName, String data) throws IOException{
        File f = new File(explorer.getDir() + "/" + fileName);

        if (!f.exists() || !f.canWrite()) return false;

        FileWriter writer = new FileWriter(f, true);
        BufferedWriter buff = new BufferedWriter(writer);
        PrintWriter printWriter = new PrintWriter(buff);

        printWriter.write(data + "\n");

        printWriter.close();
        return true;
    }

    public interface AppendExplorer{
        String getDir();
    }
}
