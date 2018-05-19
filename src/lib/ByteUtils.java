package lib;

import java.nio.ByteBuffer;

/**
 * Klasa konwersji między typem long i talbicą bajtów
 * */
public class ByteUtils {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    /**
     * Metoda zamienia long na tablicę bajtów
     * @param l liczba typu long
     * @return tablica bajtów
     * */
    public static byte[] longToByte(long l) {
        buffer.clear();
        buffer.putLong(0, l);
        return buffer.array();
    }

    /**
     * Metoda zamieniająca tablicę bajtów na typ long
     * @param bytes tablica bajtów
     * @return liczba typu long
     * */
    public static long byteToLong(byte[] bytes) {
        buffer.clear();
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }
}
