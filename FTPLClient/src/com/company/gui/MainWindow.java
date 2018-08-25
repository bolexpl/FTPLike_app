package com.company.gui;

import com.company.IconTextCellRenderer;
import com.company.explorer.IExplorer;
import com.company.explorer.LocalExplorer;
import com.company.explorer.RemoteExplorer;
import com.company.files.FilesModel;
import com.company.files.TransferModel;
import lib.Alert;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

/**
 * Klasa głównego okna
 */
public class MainWindow extends JFrame {

    private FilesModel localModel;
    private final JTable localDir;
    private final JTextField localPath;
    private FilesModel remoteModel;
    private final JTable remoteDir;
    private final JTextField remotePath;
    private LocalExplorer localExplorer;
    private RemoteExplorer remoteExplorer;
    private JTextField addressField;
    private JTextField portField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JPanel top;
    private JPanel logged;
    private JPanel contentPane;
    private ButtonGroup mode;
    private ButtonGroup transfer;
    private JLabel loggedLabel;
    private String tmpName;
    private MainWindow instance = this;

    private boolean isLogged = false;

    private String lCopy = null;
    private String rCopy = null;
    private String lCut = null;
    private String rCut = null;

    private TransferModel transferModel;

    public MainWindow() {
        super("FTP-like Client");

        URL url = getClass().getResource("/res/ftpl_launcher_big.png");
        Image img = Toolkit.getDefaultToolkit().createImage(url);
        setIconImage(img);

        ImageIcon folderIcon = new ImageIcon(getClass().getResource("/res/folder16.png"));
        ImageIcon fileIcon = new ImageIcon(getClass().getResource("/res/file16.png"));
        localExplorer = new LocalExplorer(System.getProperty("user.home"));

        contentPane = new JPanel();
        prepareTop();
        JPanel bottom = new JPanel();

        //<left>
        localPath = new JTextField(15);
        localPath.setText(localExplorer.getDir());
        localModel = new FilesModel(localExplorer);
        localDir = new JTable(localModel);
        localDir.setShowGrid(false);
        localDir.setFillsViewportHeight(true);

        JPanel left = preparePanel(localDir, localPath, true);

        TableColumnModel tableColumnModel = localDir.getColumnModel();
        tableColumnModel.getColumn(0).setCellRenderer(new IconTextCellRenderer(folderIcon, fileIcon));
        tableColumnModel.getColumn(1).setPreferredWidth(80);
        tableColumnModel.getColumn(1).setMaxWidth(120);
        tableColumnModel.getColumn(2).setMaxWidth(80);
        //</left>

        //<right>
        remotePath = new JTextField(15);
        remoteModel = new FilesModel();
        remoteDir = new JTable(remoteModel);
        remoteDir.setShowGrid(false);
        remoteDir.setFillsViewportHeight(true);
        JPanel right = preparePanel(remoteDir, remotePath, false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        tableColumnModel = remoteDir.getColumnModel();
        tableColumnModel.getColumn(0).setCellRenderer(new IconTextCellRenderer(folderIcon, fileIcon));
        tableColumnModel.getColumn(1).setPreferredWidth(80);
        tableColumnModel.getColumn(1).setMaxWidth(120);
        tableColumnModel.getColumn(2).setMaxWidth(80);
        //</right>

        //<bottom>
        transferModel = new TransferModel();
        JTable transferTable = new JTable(transferModel);

        transferTable.setPreferredScrollableViewportSize(
                new Dimension(900, 150));
        transferTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(transferTable);
        scrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        BorderFactory.createLineBorder(Color.BLACK))
        );
        bottom.add(scrollPane);
        //</bottom>

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(0.5);
        contentPane.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e component event
             */
            @Override
            public void componentResized(ComponentEvent e) {
                split.setDividerLocation(0.5);
            }
        });

        contentPane.setLayout(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(split);
        contentPane.add(scrollPane, BorderLayout.SOUTH);
        setContentPane(contentPane);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    void setTmpName(String tmpName) {
        this.tmpName = tmpName;
    }

    /**
     * Przygotowanie górnego panelu
     */
    private void prepareTop() {
        top = new JPanel();
        JPanel topA = new JPanel();
        JPanel topB = new JPanel();
        addressField = new JTextField(10);
        portField = new JTextField(4);
        userField = new JTextField(7);
        passwordField = new JPasswordField(7);

        addressField.setText("localhost");
        portField.setText("3000");

        JButton connect = new JButton("Połącz");
        connect.addActionListener(e -> swap());

        JRadioButton passive = new JRadioButton("pasywny");
        JRadioButton active = new JRadioButton("aktywny");
        passive.setActionCommand("passive");
        passive.setSelected(true);
        active.setActionCommand("active");
        mode = new ButtonGroup();
        mode.add(passive);
        mode.add(active);

        JRadioButton binary = new JRadioButton("binarny");
        JRadioButton ascii = new JRadioButton("ASCII");
        ascii.setSelected(true);
        ascii.setActionCommand("ascii");
        binary.setActionCommand("binary");
        transfer = new ButtonGroup();
        transfer.add(binary);
        transfer.add(ascii);

        topA.add(new JLabel("Serwer:"));
        topA.add(addressField);
        topA.add(new JLabel("Port:"));
        topA.add(portField);
        topA.add(new JLabel("Użytkownik:"));
        topA.add(userField);
        topA.add(new JLabel("Hasło:"));
        topA.add(passwordField);
        topB.add(new JLabel("Tryb:"));
        topB.add(passive);
        topB.add(active);
        topB.add(new JLabel("| Transfer:"));
        topB.add(ascii);
        topB.add(binary);
        topB.add(connect);

        top.setLayout(new BorderLayout());
        top.add(topA, BorderLayout.NORTH);
        top.add(topB);

        logged = new JPanel();
        loggedLabel = new JLabel();
        logged.add(loggedLabel);
        JButton disconnect = new JButton("Odłącz");
        disconnect.addActionListener(e -> swap());
        logged.add(disconnect);
    }

    /**
     * Przygotowanie lewego panelu
     */
    private JPanel preparePanel(final JTable dir,
                                final JTextField path,
                                final boolean local) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        dir.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //PPM
                if (e.getButton() == 3) {
                    int r = dir.rowAtPoint(e.getPoint());

                    if (dir.getSelectedRows().length < 2
                            && r >= 0
                            && r < dir.getRowCount()) {
                        dir.setRowSelectionInterval(r, r);
                    }

                    PopUp popUp = new PopUp(local, instance);
                    popUp.show(e.getComponent(), e.getX(), e.getY());
                    return;
                } else if (e.getButton() == 1) {
                    int r = dir.rowAtPoint(e.getPoint());

                    if (r == -1) {
                        dir.clearSelection();
                    }
                }
                int x = dir.getSelectedRow();

                //dwuklik
                if (e.getClickCount() == 2 && x != -1) {

                    FilesModel.FileCell cell = (FilesModel.FileCell) dir.getValueAt(x, 0);

                    if (cell.isDirectory()) {
                        if (local) {
                            open(localExplorer, localDir, localPath, localModel);
                        } else {
                            open(remoteExplorer, remoteDir, remotePath, remoteModel);
                        }
                    } else {
                        if (local && isLogged) {
                            put();
                        } else if(isLogged){
                            get();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        path.addActionListener(e -> {
            String p = ((JTextField) e.getSource()).getText().trim();

            if (p.charAt(p.length() - 1) == '/')
                p = p.substring(0, p.length() - 1);

            path.setText(p);

            if (local) {
                dir(localExplorer, p, localModel);
            } else {
                dir(remoteExplorer, p, remoteModel);
            }
        });

        JScrollPane jScrollPane = new JScrollPane(dir);
        panel.setLayout(new BorderLayout());
        panel.add(path, BorderLayout.NORTH);
        panel.add(jScrollPane);

        return panel;
    }

    /**
     * Podmiana górnego panelu
     */
    private void swap() {
        if (isLogged) {
            remoteExplorer.disconnect();
            contentPane.remove(logged);
            contentPane.add(top, BorderLayout.NORTH);

            remotePath.setText("");
            remoteModel.updateData(null);
            remoteModel.fireTableDataChanged();

            isLogged = false;
        } else {

            if (addressField.getText().length() == 0) {
                new Alert("Adres serwera jest wymagany");
                return;
            }

            if (portField.getText().length() == 0) {
                new Alert("Port serwera jest wymagany");
                return;
            }

            if (userField.getText().length() == 0) {
                new Alert("Login jest wymagany");
                return;
            }

            char tab[] = passwordField.getPassword();
            if (tab.length == 0) {
                new Alert("Hasło jest wymagane");
                return;
            }

            int con = connect();
            if (con == 0) {
                loggedLabel.setText("Połączono: " + addressField.getText());
                contentPane.remove(top);
                contentPane.add(logged, BorderLayout.NORTH);

                try {
                    remoteExplorer.pwd();
                    remotePath.setText(remoteExplorer.getDir());
                } catch (IOException e) {
                    new Alert("Błąd połączenia");
                }

                passwordField.setText("");
                isLogged = true;
            } else if (con == 1) {
                new Alert("Błąd połączenia");
            } else if (con == 2) {
                new Alert("Błąd logowania");
            } else {
                new Alert("Błąd ustawienia transferu");
            }
        }
        contentPane.revalidate();
        contentPane.repaint();
    }

    /**
     * Połączenie
     */
    private int connect() {
        String address = addressField.getText();
        String port = portField.getText();
        String user = userField.getText();
        String pass = String.valueOf(passwordField.getPassword());

        try {
            remoteExplorer = new RemoteExplorer(address, Integer.parseInt(port), localModel, remoteModel, transferModel);

            if (!remoteExplorer.login(user, pass)) {
                return 2;
            }
            boolean b = transfer.getSelection().getActionCommand().equals("ascii");

            if (!remoteExplorer.setTransfer(b)) {
                return 3;
            }

            b = mode.getSelection().getActionCommand().equals("passive");
            if (b) {
                if (!remoteExplorer.connectPassive()) new Alert("Błąd połączenia");
            } else if (!remoteExplorer.connectActive()) new Alert("Błąd połączenia");

            remoteModel.setExplorer(remoteExplorer);

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Otwarcie katalogu
     */
    private void open(IExplorer explorer, JTable dir, JTextField path, FilesModel model) {
        FilesModel.FileCell cell =
                (FilesModel.FileCell) dir.getValueAt(dir.getSelectedRow(), 0);

        if (cell.isDirectory()) {
            explorer.cd(cell.getName());
            path.setText(explorer.getDir());
            model.updateData();
            model.fireTableDataChanged();
        }
    }

    /**
     * Tworzenie katalogu
     */
    private void mkdir(IExplorer explorer, FilesModel model) {

        tmpName = null;
        new Prompt(this);
        if (tmpName == null || tmpName.equals("")) {
            return;
        }

        try {
            if (!explorer.mkdir(tmpName)) {
                new Alert("Nie udało sie utworzyć");
            }
            model.updateData();
            model.fireTableDataChanged();

        } catch (IOException e) {
            new Alert("Błąd");
        }
    }

    /**
     * Przejście do podanej bezwzględnej ścieżki
     */
    private void dir(IExplorer explorer, String path, FilesModel model) {
        try {
            if (!explorer.setDir(path)) {
                new Alert("Błąd");
            }
            model.updateData();
            model.fireTableDataChanged();

        } catch (IOException e) {
            new Alert("Błąd otwarcia katalogu");
        }
    }

    /**
     * Usuwanie plików i katalogów
     */
    private void rm(IExplorer explorer, FilesModel model, JTable dir) {
        try {
            int[] indexes = dir.getSelectedRows();

            FilesModel.FileCell cell;
            for (int x : indexes) {
                cell = (FilesModel.FileCell) dir.getValueAt(x, 0);
                if (!cell.getName().equals("..") && !explorer.rm(explorer.getDir() + "/" + cell.getName()))
                    new Alert("Błąd");

            }
            model.updateData();
            model.fireTableDataChanged();
        } catch (IOException e) {
            new Alert("Błąd");
        }
    }

    /**
     * Zmiana nazwy
     */
    private void mv(IExplorer explorer, FilesModel model, JTable dir) {
        FilesModel.FileCell cell =
                (FilesModel.FileCell) dir.getValueAt(dir.getSelectedRow(), 0);

        tmpName = null;
        new Prompt(this);
        if (tmpName == null || tmpName.equals("")) {
            return;
        }

        try {
            if (!explorer.mv(explorer.getDir() + "/" + cell.getName(),
                    explorer.getDir() + "/" + tmpName))
                new Alert("Błąd");

            model.updateData();
            model.fireTableDataChanged();
        } catch (IOException e) {
            new Alert("Błąd");
        }
    }

    /**
     * Pobranie pliku
     */
    private void get() {
        trans(true);
    }

    /**
     * Wysłanie pliku
     */
    private void put() {
        trans(false);
    }

    /**
     * Ustawienie transferu pliku
     */
    private void trans(boolean get) {

        int[] indexes;

        if (get)
            indexes = remoteDir.getSelectedRows();
        else
            indexes = localDir.getSelectedRows();

        FilesModel.FileCell cell;

        for (int x : indexes) {
            if (get) {
                cell = (FilesModel.FileCell) remoteDir.getValueAt(x, 0);
                if (!cell.getName().equals("..") && !cell.isDirectory())
                    remoteExplorer.get(cell.getName(),
                            localExplorer.getDir());
            } else {
                cell = (FilesModel.FileCell) localDir.getValueAt(x, 0);
                if (!cell.getName().equals("..") && !cell.isDirectory())
                    remoteExplorer.put(cell.getName(),
                            localExplorer.getDir());
            }
        }
    }

    /**
     * Metoda do wklejania pliku po wycięciu lub skopiowaniu
     *
     * @param local plik lokalny lub zdalny
     * @param path2 ścieżka docelowa
     */
    private void paste(boolean local, String path2) {

        if (local) {
            if (lCut == null) {
                paste(localExplorer, lCopy, path2, localModel, true);
            } else {
                paste(localExplorer, lCut, path2, localModel, false);
            }
        } else {
            if (rCut == null) {
                paste(remoteExplorer, rCopy, path2, remoteModel, true);
            } else {
                paste(remoteExplorer, rCut, path2, remoteModel, false);
            }
        }

    }

    /**
     * Metoda do wklejania pliku po wycięciu lub skopiowaniu
     *
     * @param explorer explorer plików
     * @param path1    ścieżka źródłowa
     * @param path2    ścieżka docelowa
     * @param model    model dla tabeli plików
     * @param copy     kopiowanie lub przenoszenie
     */
    private void paste(final IExplorer explorer, final String path1,
                       final String path2, final FilesModel model,
                       final boolean copy) {
        new Thread() {

            @Override
            public void run() {
                String tab[] = path1.split("/");
                try {
                    if (copy)
                        explorer.copy(path1, path2 + "/" + tab[tab.length - 1]);
                    else
                        explorer.mv(path1, path2 + "/" + tab[tab.length - 1]);
                    model.updateData();
                    model.fireTableDataChanged();
                } catch (IOException e) {
                    new Alert("Nie można wkleić");
                }
            }
        }.start();
    }

    /**
     * Metoda tworzy pusty plik
     *
     * @param local plik lokalny lub zdalny
     */
    private void touch(boolean local) {
        try {
            tmpName = null;
            new Prompt(this);

            if (local) {
                if (!localExplorer.touch(tmpName)) {
                    new Alert("Błąd");
                }
                localModel.updateData();
                localModel.fireTableDataChanged();
            } else {
                if (!remoteExplorer.touch(tmpName)) {
                    new Alert("Błąd");
                }
                remoteModel.updateData();
                remoteModel.fireTableDataChanged();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda dopisująca do pliku
     *
     * @param explorer explorer plików
     * @param dir      tabela plików
     */
    private void append(IExplorer explorer, JTable dir) {
        FilesModel.FileCell cell =
                (FilesModel.FileCell) dir.getValueAt(dir.getSelectedRow(), 0);

        try {
            tmpName = null;

            new LinePrompt(this);

            if (tmpName != null && !explorer.append(cell.getName(), tmpName))
                new Alert("Błąd");

        } catch (IOException e) {
            new Alert("Błąd");
        }
    }

    /**
     * Klasa menu kontekstowego
     */
    class PopUp extends JPopupMenu implements ActionListener {

        JMenuItem get;
        JMenuItem put;
        JMenuItem delete;
        JMenuItem changeName;
        JMenuItem open;
        JMenuItem mkdir;
        JMenuItem hidden;
        JMenuItem refresh;
        JMenuItem copy;
        JMenuItem cut;
        JMenuItem paste;
        JMenuItem create;
        JMenuItem append;
        boolean local;
        MainWindow mainWindow;

        PopUp(boolean local, MainWindow mainWindow) {
            this.mainWindow = mainWindow;
            this.local = local;
            get = new JMenuItem("Wyślij");
            put = new JMenuItem("Pobierz");
            open = new JMenuItem("Otwórz");
            mkdir = new JMenuItem("Utwórz katalog");
            hidden = new JMenuItem("Pokaż/Ukryj ukryte");
            refresh = new JMenuItem("Odśwież");
            changeName = new JMenuItem("Zmień nazwę");
            delete = new JMenuItem("Usuń");
            copy = new JMenuItem("Kopiuj");
            cut = new JMenuItem("Wytnij");
            paste = new JMenuItem("Wklej");
            create = new JMenuItem("Utwórz plik");
            append = new JMenuItem("Dopisz do pliku");

            open.addActionListener(this);
            mkdir.addActionListener(this);
            hidden.addActionListener(this);
            refresh.addActionListener(this);
            changeName.addActionListener(this);
            delete.addActionListener(this);
            copy.addActionListener(this);
            cut.addActionListener(this);
            paste.addActionListener(this);
            create.addActionListener(this);
            append.addActionListener(this);

            if (local) {
                get.addActionListener(this);
                add(get);
            } else {
                put.addActionListener(this);
                add(put);
            }
            add(open);
            add(create);
            add(mkdir);
            add(copy);
            add(cut);
            add(paste);
            add(hidden);
            add(refresh);
            add(append);
            add(changeName);
            add(delete);

            if (local)
                put.setEnabled(false);
            else
                get.setEnabled(false);

            if ((local && lCopy == null && lCut == null) ||
                    (!local && rCopy == null && rCut == null)) {
                paste.setEnabled(false);
            }

            if (local) {
                if (localDir.getSelectedRows().length == 1) {
                    int index = localDir.getSelectedRow();
                    String s = (String) localDir.getValueAt(index, 2);

                    if (s.equals("katalog")) append.setEnabled(false);
                } else {
                    append.setEnabled(false);
                }
            } else if (remoteDir.getSelectedRows().length == 1) {

                int index = remoteDir.getSelectedRow();
                String s = (String) remoteDir.getValueAt(index, 2);
                if (s.equals("katalog")) append.setEnabled(false);

            } else {
                append.setEnabled(false);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            switch (actionCommand) {
                case "Otwórz":

                    if (local && localDir.getSelectedRows().length == 1)
                        open(localExplorer, localDir, localPath, localModel);
                    else if (!local && remoteDir.getSelectedRows().length == 1)
                        open(remoteExplorer, remoteDir, remotePath, remoteModel);

                    break;
                case "Wyślij":

                    if (local && localDir.getSelectedRows().length > 0 && isLogged)
                        put();

                    break;
                case "Pobierz":

                    if (!local && remoteDir.getSelectedRows().length > 0)
                        get();

                    break;
                case "Utwórz katalog":

                    if (local)
                        mkdir(localExplorer, localModel);
                    else
                        mkdir(remoteExplorer, remoteModel);

                    tmpName = null;
                    break;
                case "Pokaż/Ukryj ukryte":

                    if (local) {
                        localExplorer.invertHidden();
                        localModel.updateData();
                        localModel.fireTableDataChanged();
                    } else {
                        remoteExplorer.invertHidden();
                        remoteModel.updateData();
                        remoteModel.fireTableDataChanged();
                    }

                    contentPane.revalidate();
                    contentPane.repaint();

                    break;
                case "Odśwież":

                    if (local) {
                        localModel.updateData();
                        localModel.fireTableDataChanged();
                    } else {
                        remoteModel.updateData();
                        remoteModel.fireTableDataChanged();
                    }

                    break;
                case "Zmień nazwę":

                    if (local)
                        mv(localExplorer, localModel, localDir);
                    else
                        mv(remoteExplorer, remoteModel, remoteDir);

                    break;
                case "Usuń":

                    if (local)
                        rm(localExplorer, localModel, localDir);
                    else
                        rm(remoteExplorer, remoteModel, remoteDir);

                    break;
                case "Kopiuj":

                    setPaths(true);

                    break;
                case "Wytnij":

                    setPaths(false);

                    break;
                case "Wklej":

                    p();

                    break;
                case "Utwórz plik":

                    touch(local);

                    break;
                case "Dopisz do pliku":

                    if (local && localDir.getSelectedRows().length == 1)
                        append(localExplorer, localDir);
                    else if (!local && remoteDir.getSelectedRows().length == 1)
                        append(remoteExplorer, remoteDir);

                    break;
            }
        }

        /**
         * Metoda do wklejania pliku
         */
        private void p() {
            String path2;
            if (local)
                path2 = getDirPath(localExplorer, localDir);
            else
                path2 = getDirPath(remoteExplorer, remoteDir);

            if (path2 == null) return;

            paste(local, path2);

            if (local) {
                lCopy = null;
                lCut = null;
                localModel.updateData();
                localModel.fireTableDataChanged();
            } else {
                rCopy = null;
                rCut = null;
                remoteModel.updateData();
                remoteModel.fireTableDataChanged();
            }
        }

        /**
         * Metoda ustawia ścieżki do kopiowania i przenoszenia pliku
         */
        private void setPaths(boolean copy) {
            if (local && localDir.getSelectedRows().length == 1) {
                String path = getFilePath(localExplorer, localDir);
                if (copy) {
                    lCut = null;
                    lCopy = path;
                } else {
                    lCopy = null;
                    lCut = path;
                }

            } else if (!local && remoteDir.getSelectedRows().length == 1) {
                String path = getFilePath(remoteExplorer, remoteDir);
                if (copy) {
                    rCut = null;
                    rCopy = path;
                } else {
                    rCopy = null;
                    rCut = path;
                }
            }
        }

        private String getDirPath(IExplorer explorer, JTable dir) {

            if (dir.getSelectedRow() == -1) {
                return explorer.getDir();
            }
            if (dir.getSelectedRows().length == 1) {

                int index = dir.getSelectedRow();

                if (dir.getValueAt(index, 2).equals("plik")) {
                    return explorer.getDir();
                }

                FilesModel.FileCell cell =
                        (FilesModel.FileCell) dir.getValueAt(index, 0);

                return explorer.getDir() + "/" + cell.getName();
            }

            return null;
        }

        private String getFilePath(IExplorer explorer, JTable dir) {
            FilesModel.FileCell cell =
                    (FilesModel.FileCell) dir.getValueAt(dir.getSelectedRow(), 0);
            return explorer.getDir() + "/" + cell.getName();
        }
    }
}
