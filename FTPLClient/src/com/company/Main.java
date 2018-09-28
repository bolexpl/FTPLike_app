package com.company;

import com.company.gui.MainWindow;
import lib.Protocol;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * <h1>Klient FTPL</h1>
 * Klient do obsługi autorskiego protokołu FTPL
 *
 * @author Arkadiusz Bolesta
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {

        if (args.length > 0) {
            for (String arg : args) {
                switch (arg) {
                    case "-s":
                    case "--system":
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            System.err.println("Nie udała się zmiana wyglądu: ");
                        }
                        break;
                    case "-h":
                    case "--help":
                        System.out.println("Serwer protokołu FTPL");
                        System.out.println("Argumenty opcjonalne:");
                        System.out.println("-h   --help      wyświetlenie ekranu pomocy");
                        System.out.println("-s   --system    systemowy wygląd okien");
                        System.exit(0);
                }
            }
        }

        EventQueue.invokeLater(MainWindow::new);
    }

//    /**
//     * Metoda sprawdzająca czy port jest dostępny do użycia.
//     *
//     * @param port Numer portu
//     * @return Czy dostępny port
//     */
//    public static boolean isPortAvailable(int port) {
//
//        if (port < Protocol.MIN_PORT_NUMBER || port > Protocol.MAX_PORT_NUMBER) {
//            return false;
//        }
//
//        ServerSocket ss = null;
//        try {
//            ss = new ServerSocket(port);
//            ss.setReuseAddress(true);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (ss != null) {
//                try {
//                    ss.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return false;
//    }

//    /**
//     * Metoda sprawdzająca czy jest dostęp do katalogu.
//     *
//     * @param dir Ścieżka do katalogu
//     * @return Czy dostępny katalog
//     */
//    public static boolean isAccess(String dir) {
//        File f = new File(dir);
//        return f.exists() && f.isDirectory() && f.canRead() && f.canExecute();
//    }
}
