package com.ftpl.client;

import com.ftpl.client.gui.MainWindow;
import com.ftpl.lib.Utils;

import javax.swing.*;
import java.awt.*;

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
                        Utils.path = args[i];
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
