package lib;

/**
 * Klasa ze stałymi opisującymi protokół
 */
public class Protocol {

    public static final String USER = "USER";
    public static final String PASSWORD = "PASS";
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String ACTIVE = "ACTIVE";
    public static final String PASSIV = "PASSIV";
    public static final String PORT = "PORT";
    public static final String LIST = "LIST";
    public static final String CD = "CD";
    public static final String MKDIR = "MK";
    public static final String RM = "RM";
    public static final String EOF = "EOF";
    public static final String CANCEL = "CANCEL";
    public static final String EXIT = "EXIT";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String MV = "MV";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String DIR = "DIR";
    public static final String FILE = "FILE";
    public static final String TRANSFER = "TRANSFER";
    public static final String BINARY = "BINARY";
    public static final String ASCII = "ASCII";
    public static final String PWD = "PWD";
    public static final String CP = "CP";
    public static final String TOUCH = "TOUCH";
    public static final String APPEND = "APPEND";


    public static final int MIN_PORT_NUMBER = 1024;
    public static final int MAX_PORT_NUMBER = 65535;

    /**
     * Rozmiar pakietu w transferze binarnym
     */
    public static final int PACKET_LENGTH = 512;

    private Protocol() {
    }
}
