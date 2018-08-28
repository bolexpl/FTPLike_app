package com.company.clients;

import com.company.Main;
import com.company.NewFile;
import com.company.explorer.LocalExplorer;
import lib.ByteUtils;
import lib.Protocol;
import com.company.db.SQLiteJDBC;
import com.company.db.User;
import com.company.gui.MainWindow;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Wątek obsługujący klienta
 */
public class ClientThread extends Thread {

    private String login;
    private ClientsModel model;

    private Socket controlSocket;
    private Socket dataSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private BufferedReader inASCII;
    private BufferedWriter outASCII;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    private LocalExplorer explorer;
    private MainWindow window;

    private DataReceiveThread dataReceiveThread;
    private DataSendThread dataSendThread;

    private boolean ascii = true;
    private boolean connected = true;

    public ClientThread(Socket controlSocket, MainWindow window, ClientsModel model)
            throws IOException {
        this.controlSocket = controlSocket;
        this.window = window;
        this.model = model;

        reader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
        if (Main.path != null) {
            explorer = new LocalExplorer(Main.path);
        } else if (Main.debug) {
            explorer = new LocalExplorer("/home/bolek/test");
        } else {
            explorer = new LocalExplorer(System.getProperty("user.home"));
        }
        start();
    }

    /**
     * Getter dla controlSocket
     *
     * @return constrolSocket
     */
    public Socket getControlSocket() {
        return controlSocket;
    }

    @Override
    public void run() {
        try {
            if (login()) {
                transferMode();
                listen();
            } else {
                write(Protocol.ERROR);
                disconnect();
            }
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }
    }

    /**
     * Metoda służąca do logowania na serwerze
     *
     * @return sukces
     * @throws IOException wyjątek
     */
    private boolean login() throws IOException {

        String[] args;
        String password;

        args = reader.readLine().split(" ");

        if (args.length == 2 && args[0].equals(Protocol.USER)) {
            login = args[1];
            write(Protocol.OK);

            args = reader.readLine().split(" ");

            if (args.length == 2 && args[0].equals(Protocol.PASSWORD)) {
                password = args[1];

                SQLiteJDBC db = SQLiteJDBC.getInstance();
                try {
                    List<User> list = db.select(login, password);

                    if (list.size() == 0) {
                        write(Protocol.ERROR);
                        return false;
                    }

                    write(Protocol.OK);
                    model.add(this);
                    return true;
                } catch (SQLException e) {
                    write(Protocol.ERROR);
                }
            }
        }
        return false;
    }

    /**
     * Metoda ustawiająca typ transferu
     *
     * @throws IOException wyjątek
     */
    private void transferMode() throws IOException {
        String args[] = reader.readLine().split(" ");

        if (args.length == 2 && args[0].equals(Protocol.TRANSFER)) {
            ascii = args[1].equals(Protocol.ASCII);
            write(Protocol.OK);
        } else {
            write(Protocol.ERROR);
            disconnect();
        }
    }

    /**
     * Metoda nasłuchująca komunikatów sterujacych od klienta
     *
     * @throws IOException wyjątek
     */
    private void listen() throws IOException {
        String args[];

        while (connected) {

            String s = reader.readLine();

            if (s == null) {
                disconnect();
                return;
            } else
                args = s.split(" ");


            if (args.length == 0) continue;

            switch (args[0]) {
                case Protocol.EXIT:
                    disconnect();
                    break;

                case Protocol.PASSIV:
                    if (out == null && outASCII == null) {
                        passiv();
                    } else write(Protocol.ERROR);
                    break;

                case Protocol.ACTIVE:
                    if (out == null && outASCII == null && args.length == 2) {
                        active(args[1]);
                    } else write(Protocol.ERROR);
                    break;

                case Protocol.LIST:
                    if (args.length == 2) {
                        ls(args[1]);
                    } else if (args.length == 1) {
                        ls(Protocol.FALSE);
                    }
                    break;

                case Protocol.CD:
                    if (cd(reader.readLine())) {
                        write(Protocol.OK);
                        write(explorer.getDir());
                    } else {
                        write(Protocol.ERROR);
                    }
                    break;

                case Protocol.GET:
                    get(reader.readLine());
                    break;

                case Protocol.PUT:
                    put(reader.readLine());
                    break;

                case Protocol.MKDIR:

                    mkdir(reader.readLine());
                    break;
                case Protocol.RM:
                    rm(reader.readLine());
                    break;

                case Protocol.MV:
                    mv(reader.readLine(), reader.readLine());
                    break;

                case Protocol.DIR:
                    dir(reader.readLine());
                    break;

                case Protocol.PWD:
                    pwd();
                    break;

                case Protocol.CP:
                    copy(reader.readLine(), reader.readLine());
                    break;

                case Protocol.TOUCH:
                    touch(reader.readLine());
                    break;

                case Protocol.APPEND:
                    append(reader.readLine(), reader.readLine());
                    break;

                default:
                    write(Protocol.ERROR);
                    disconnect();
            }
        }
    }

    /**
     * Metoda do połączenia w trybie pasywnym
     *
     * @throws IOException wyjątek
     */
    private void passiv() throws IOException {
        int x;
        Random rand = new Random();
        do {
            x = rand.nextInt(Protocol.MAX_PORT_NUMBER);
        } while (!Main.isPortAvailable(x));

        ServerSocket serverSocket = new ServerSocket(x);
        write(Protocol.PORT + " " + x);
        dataSocket = serverSocket.accept();
        serverSocket.setReuseAddress(true);
        serverSocket.close();

        write(Protocol.OK);

        connectStreams();
    }

    /**
     * Metoda do połączenia w trybie aktywnym
     *
     * @throws IOException wyjątek
     */
    private void active(String arg) throws IOException {
        int port = Integer.parseInt(arg);
        System.out.println(port);

        try {
            dataSocket = new Socket(controlSocket.getInetAddress(), port);
            write(Protocol.OK);
        } catch (IOException e) {
            write(Protocol.ERROR);
        }

        connectStreams();
    }

    /**
     * Metoda wysyłająca listę plików i katalogów w katalogu roboczym
     *
     * @throws IOException wyjątek
     */
    private void ls(String arg) throws IOException {
        List<File> list;

        if (arg.equals(Protocol.TRUE)) {
            list = explorer.listFiles(true);
        } else {
            list = explorer.listFiles(false);
        }

        for (File f : list) {
            if (f.isDirectory())
                write(Protocol.DIR);
            else
                write(Protocol.FILE);

            write(f.getName());

            if (f.isHidden())
                write(Protocol.TRUE);
            else
                write(Protocol.FALSE);

            write(Long.toString(f.length()));
        }

        write(Protocol.EOF);
    }

    /**
     * Metoda tworząca pusty plik
     *
     * @param name Nazwa nowego pliku
     * @throws IOException wyjątek
     */
    private void touch(String name) throws IOException {
        if (explorer.touch(name))
            write(Protocol.OK);
        else
            write(Protocol.ERROR);
    }

    /**
     * Metoda do otwierania katalogu
     *
     * @param dir Nazwa katalogu
     */
    private boolean cd(String dir) {

        if (dir.charAt(0) == '/') return explorer.setDir(dir);
        else if (dir.equals("..")) return explorer.cdParent();
        else return explorer.cd(dir);
    }

    /**
     * Metoda dopisująca ciąg znaków do pliku
     *
     * @param fileName Nazwa pliku
     * @param data     Ciąg znaków
     * @throws IOException wyjątek
     */
    private void append(String fileName, String data) throws IOException {
        if (explorer.append(fileName, data))
            write(Protocol.OK);
        else
            write(Protocol.ERROR);
    }

    /**
     * Metoda do usuwania pliku
     *
     * @throws IOException wyjątek
     */
    private void rm(String fileName) throws IOException {
        if (explorer.rm(fileName))
            write(Protocol.OK);
        else
            write(Protocol.ERROR);
    }

    /**
     * Metoda wysyłająca aktualną ścieżkę
     *
     * @throws IOException wyjątek
     */
    private void pwd() throws IOException {
        write(explorer.getDir());
    }

    /**
     * Metoda zmieniająca aktualną ścieżkę w obiekcie explorera
     *
     * @param path Ścieżka do katalogu
     * @throws IOException wyjątek
     */
    private void dir(String path) throws IOException {
        if (explorer.setDir(path))
            write(Protocol.OK);
        else
            write(Protocol.ERROR);
    }

    /**
     * Metoda do tworzenia katalogu
     *
     * @param dir Nazwa katalogu
     * @throws IOException wyjątek
     */
    private void mkdir(String dir) throws IOException {
        if (explorer.mkdir(dir))
            write(Protocol.OK);
        else
            write(Protocol.ERROR);
    }

    /**
     * Metoda do pobierania pliku z serwera
     *
     * @param file Ścieżka pliku
     * @throws IOException wyjątek
     */
    private void get(String file) throws IOException {
        File f = new File(file);
        System.out.println(file);
        if (f.isDirectory()) return;

        if (!connected) return;

        if (!f.exists())
            write(Protocol.ERROR);
        else {
            write(Protocol.OK);
            dataSendThread.addFile(f);
        }
    }

    /**
     * Metoda do wysyłania pliku na serwer
     *
     * @param path Ścieżka pliku
     * @throws IOException wyjątek
     */
    private void put(String path) throws IOException {
        File f = new File(path);

        if (!connected) return;

        if (f.exists() && f.isDirectory()) {
            write(Protocol.ERROR);
            return;
        }

        dataReceiveThread.addFile(new NewFile(path));
    }

    /**
     * Metoda łącząca strumienie
     *
     * @throws IOException wyjątek
     */
    private void connectStreams() throws IOException {
        if (ascii) {
            outASCII = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
            inASCII = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        } else {
            in = new BufferedInputStream(new DataInputStream(dataSocket.getInputStream()));
            out = new BufferedOutputStream(new DataOutputStream(dataSocket.getOutputStream()));
        }
        dataReceiveThread = new DataReceiveThread(inASCII, in);
        dataReceiveThread.start();
        dataSendThread = new DataSendThread(outASCII, out);
        dataSendThread.start();
    }

    /**
     * Metoda wysyłająca polecenia przez socket sterujący
     *
     * @param s Dane do wysłania
     * @throws IOException wyjątek
     */
    private void write(String s) throws IOException {
        System.out.println(s);
        writer.write(s);
        writer.newLine();
        writer.flush();
    }

    /**
     * Metoda do przenoszenia i zmiany nazwy plików i katalogów
     *
     * @param oldFile Stara ścieżka z nazwą
     * @param newFile Nowa ścieżka z nazwą
     * @throws IOException wyjątek
     */
    private void mv(String oldFile, String newFile) throws IOException {
        if (explorer.mv(oldFile, newFile))
            write(Protocol.OK);
        else
            write(Protocol.ERROR);
    }

    /**
     * Metoda służąca do kopiowania plików między katalogami.
     * Metoda nie służy do pobierania i wysyłania plików.
     *
     * @param path1 Ścieżka źródłowa
     * @param path2 Ścieżka docelowa
     * @throws IOException wyjątek
     */
    private void copy(final String path1, final String path2) throws IOException {
        if (explorer.copy(path1, path2)) {
            write(Protocol.OK);
        } else {
            write(Protocol.ERROR);
        }
    }

    /**
     * Metoda kończąca połączenie
     */
    void disconnect() {
        try {
            connected = false;


            if (dataReceiveThread != null)
                dataReceiveThread.setStop();
            if (dataSendThread != null)
                dataSendThread.setStop();

            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();

            if (inASCII != null)
                inASCII.close();
            if (outASCII != null)
                outASCII.close();

            if (in != null)
                in.close();
            if (out != null)
                out.close();

            if (controlSocket != null)
                controlSocket.close();
            if (dataSocket != null)
                dataSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        model.remove(this);
        window.addColoredText("Disconnected " + controlSocket.getInetAddress().getHostAddress(), Color.RED);
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }

    /**
     * Getter dla loginu
     */
    String getLogin() {
        return login;
    }

    /**
     * Klasa wątku do wysyłania plików
     */
    class DataSendThread extends Thread {
        private boolean running = true;
        private BufferedWriter outASCII;
        private BufferedOutputStream out;
        private volatile List<File> list;

        DataSendThread(BufferedWriter outASCII, BufferedOutputStream out) {
            this.list = new LinkedList<>();
            this.outASCII = outASCII;
            this.out = out;
        }

        /**
         * Metoda zatrzymująca wątek
         */
        void setStop() {
            running = false;
        }

        /**
         * Metoda dodająca pliki do kolejki wysyłania
         *
         * @param f Plik
         */
        void addFile(File f) {
            list.add(f);
        }

        @Override
        public void run() {
            while (running) {
                if (list.size() > 0) {

                    File f = list.remove(0);

                    try {
                        if (ascii) {
                            sendASCII(f);
                        } else {
                            sendBinary(f);
                        }
                    } catch (IOException e) {
                        disconnect();
                    }
                }
            }
        }

        /**
         * Metoda wysyłająca plik w trybie ASCII
         *
         * @param f Plik do wysłania
         * @throws IOException wyjątek
         */
        private void sendASCII(File f) throws IOException {
            BufferedReader buff = new BufferedReader(new FileReader(f));

            String s;

            send(Long.toString(f.length()));

            while ((s = buff.readLine()) != null) {

                if (s.equals(Protocol.EOF)) {
                    send("\\" + s);
                } else {
                    send(s);
                }
            }
            send(Protocol.EOF);

            buff.close();
        }

        /**
         * Metoda wysyłająca plik w trybie binarnym
         *
         * @param f Plik do wysłania
         * @throws IOException wyjątek
         */
        private void sendBinary(File f) throws IOException {
            BufferedInputStream buff = new BufferedInputStream(new FileInputStream(f));

            int k;
            byte[] data = new byte[Protocol.PACKET_LENGTH];

            long size = f.length();
            send(ByteUtils.longToByte(size));

            while (size > 0) {
                k = buff.read(data, 0, Protocol.PACKET_LENGTH);
                send(data, k);
                size -= k;
            }

            buff.close();

//            while ((k = buff.read(data, 0, Protocol.PACKET_LENGTH)) != -1) {
//                String s = new String(data, 0, k);
//
//                if (s.equals(Protocol.EOF)) {
//                    s = "\\" + s;
//                    data = s.getBytes();
//                    k++;
//                }
//                send(data, k);
//                old = k;
//            }
//            if (old == Protocol.PACKET_LENGTH) send(Protocol.EOF.getBytes());
        }

        /**
         * Metoda do wysyłania danych przez dataSocket
         *
         * @param data ciąg ASCII do wysłania
         * @throws IOException wyjątek
         */
        void send(String data) throws IOException {
            outASCII.write(data);
            outASCII.newLine();
            outASCII.flush();
        }

        /**
         * Metoda do wysyłania danych przez dataSocket
         *
         * @param data dane binarne
         * @throws IOException wyjątek
         */
        void send(byte[] data) throws IOException {
            send(data, data.length);
        }

        /**
         * Metoda do wysyłania danych przez dataSocket
         *
         * @param data dane binarne
         * @param len  długość danych w bajtach
         * @throws IOException wyjątek
         */
        void send(byte[] data, int len) throws IOException {
            out.write(data, 0, len);
            out.flush();
        }
    }

    /**
     * Klasa wątku do odbierania plików
     */
    class DataReceiveThread extends Thread {
        private boolean running = true;
        private BufferedReader inASCII;
        private BufferedInputStream in;
        private volatile List<NewFile> list;

        DataReceiveThread(BufferedReader inASCII, BufferedInputStream in) {
            this.inASCII = inASCII;
            this.in = in;
            list = new LinkedList<>();
        }

        /**
         * Metoda zatrzymująca wątek
         */
        void setStop() {
            running = false;
        }

        /**
         * Metoda dodająca pliki do kolejki wysyłania
         *
         * @param f Plik
         */
        void addFile(NewFile f) {
            list.add(f);
        }

        @Override
        public void run() {
            while (running) {
                if (list.size() > 0) {

                    NewFile nf = list.remove(0);
                    File f = new File(nf.getPath());

                    try {

                        if (!f.exists()) {
                            if (f.createNewFile())
                                write(Protocol.OK);
                            else {
                                write(Protocol.ERROR);
                                continue;
                            }
                        } else
                            write(Protocol.OK);

                        if (ascii) {
                            receiveASCII(f);
                        } else {
                            receiveBinary(f);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Metoda odbierająca plik w trybie ASCII
         *
         * @param f Plik do odebrania
         * @throws IOException wyjątek
         */
        private void receiveASCII(File f) throws IOException {
            BufferedWriter buff =
                    new BufferedWriter(
                            new FileWriter(f));

            long size = Long.parseLong(inASCII.readLine());

            String s;
            while (!(s = inASCII.readLine()).equals(Protocol.EOF)) {
                if (s.equals("\\" + Protocol.EOF))
                    buff.write(s.substring(1, s.length()));
                else
                    buff.write(s);
                buff.newLine();
                buff.flush();
            }

            buff.close();
        }

        /**
         * Metoda odbierająca plik w trybie binarnym
         *
         * @param f Plik do odebrania
         * @throws IOException wyjątek
         */
        private void receiveBinary(File f) throws IOException {

            BufferedOutputStream buff =
                    new BufferedOutputStream(new FileOutputStream(f));


            byte[] data = new byte[Protocol.PACKET_LENGTH];
            int k;

            byte[] bytes = new byte[Long.BYTES];
            k = in.read(bytes);
            long size = ByteUtils.byteToLong(bytes);

            while (size > 0) {
                k = in.read(data);
                buff.write(data, 0, k);
                size -= k;
            }
            buff.flush();

            buff.close();
        }
    }
}
