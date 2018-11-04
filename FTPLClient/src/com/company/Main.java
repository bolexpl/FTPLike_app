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

    public static String path = System.getProperty("user.home");

    public static void main(String[] args) {

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-s":
                    case "--system":
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            System.err.println("Nie udała się zmiana wyglądu: ");
                        }
                        break;
                    case "-r":
                    case "--root":
                        if (++i == args.length) {
                            System.out.println("Złe argumenty");
                            System.exit(0);
                        }
                        path = args[i];
                        break;
                    case "-h":
                    case "--help":
                        System.out.println("Serwer protokołu FTPL");
                        System.out.println("Argumenty opcjonalne:");
                        System.out.println("-h          --help          wyświetlenie ekranu pomocy");
                        System.out.println("-r <folder> --root <folder> ustawienie ścieżki początkowej");
                        System.out.println("-s          --system        systemowy wygląd okien");
                        System.exit(0);
                }
            }
        }

        EventQueue.invokeLater(MainWindow::new);
    }
}
