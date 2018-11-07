package com.ftpl.lib;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

/**
 * Klasa konwersji między typem long i talbicą bajtów
 */
public class Utils {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    public static boolean debug = true;

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
     * */
    public static boolean isAccess(String dir) {
        File f = new File(dir);
        return f.exists() && f.isDirectory() && f.canRead() && f.canExecute();
    }

    /**
     * Metoda sprawdzająca równość tablic o różnych długościach
     *
     * @param bigger większa tablica
     * @param smaller mniejsza tablica
     * @return Czy wartości są równe
     * */
    public static boolean equalsArrays(byte bigger[], byte smaller[]) {

        for (int i = 0; i < smaller.length; i++)
            if (bigger[i] != smaller[i])
                return false;

        return true;
    }
}
