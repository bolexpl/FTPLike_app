package com.company.server;

import com.company.server.gui.CommandLine;
import com.company.server.gui.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>Serwer FTPL</h1>
 * Serwer do obsługi autorskiego protokołu FTPL
 *
 * @author Arkadiusz Bolesta
 * @version 1.0
 */
public class Main {

    public static boolean debug = false;
    public static String path = System.getProperty("user.home");
    public static String database = "base.db";

    public static void main(String[] args) {


        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-d":
                    case "--debug":
                        debug = true;
                        break;
                    case "-r":
                    case "--root":
                        if (++i == args.length) {
                            System.out.println("Złe argumenty");
                            System.exit(0);
                        }
                        path = args[i];
                        break;
                    case "-s":
                    case "--system":
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            System.err.println("Nie udała się zmiana wyglądu.");
                        }
                        break;
                    case "-b":
                    case "--database":
                        if (++i == args.length) {
                            System.out.println("Złe argumenty");
                            System.exit(0);
                        }
                        String[] var = database.split("\\.");
                        if (var[var.length - 1].equals("db"))
                            database = args[i];
                        else
                            database = args[i] + ".db";
                        break;
                    case "-n":
                    case "--no-gui":
                        new CommandLine();
                        return;
                    case "-h":
                    case "--help":
                        System.out.println("Serwer protokołu FTPL");
                        System.out.println("Argumenty opcjonalne:");
                        System.out.println("-h          --help             wyświetlenie ekranu pomocy");
                        System.out.println("-d          --debug            tryb debugowania");
                        System.out.println("-r <folder> --root <folder>    ustawienie ścieżki początkowej");
                        System.out.println("-s          --system           systemowy wygląd okien");
                        System.out.println("-b <plik>   --database <plik>  plik bazy danych");
                        System.out.println("-n          --no-gui           plik bazy danych");
                        System.exit(0);
                }
            }
        }
        EventQueue.invokeLater(() -> {
            try {
                new MainWindow();
            } catch (HeadlessException e) {
                new CommandLine();
            }
        });
    }
}
