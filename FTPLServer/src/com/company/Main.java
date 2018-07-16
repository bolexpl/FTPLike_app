package com.company;

import com.company.gui.MainWindow;
import lib.Base64Coder;
import lib.Protocol;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;

/**
 * <h1>Serwer FTPL</h1>
 * Serwer do obsługi autorskiego protokołu FTPL
 *
 * @author Arkadiusz Bolesta
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            System.err.println("Nie udała się zmiana wyglądu: ");
//        }
        EventQueue.invokeLater(MainWindow::new);
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
}
