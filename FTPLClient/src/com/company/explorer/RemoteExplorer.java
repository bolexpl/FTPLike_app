package com.company.explorer;

import com.company.Main;
import lib.ByteUtils;
import lib.Protocol;
import com.company.files.*;
import lib.Alert;
import lib.Base64Coder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Klasa odpowiedzialna za komunikację z serwerem
 */
public class RemoteExplorer implements IExplorer {

    private String dir;
    private boolean showHidden = false;

    private Socket controlSocket;
    private Socket dataSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private BufferedReader inASCII;
    private BufferedWriter outASCII;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private boolean ascii = true;

    private DataSendThread dataSendThread;
    private DataReceiveThread dataReceiveThread;

    private FilesModel localModel;
    private FilesModel remoteModel;

    private TransferModel transferModel;

    public RemoteExplorer(String ip, int port,
                          FilesModel localModel, FilesModel remoteModel,
                          TransferModel transferModel)
            throws IOException {
        controlSocket = new Socket(ip, port);

        writer = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));

        this.localModel = localModel;
        this.remoteModel = remoteModel;
        this.transferModel = transferModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDir() {
        return dir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDir(String dir) throws IOException {
        write(Protocol.DIR);
        write(dir);
        return reader.readLine().equals(Protocol.OK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(String path1, String path2) throws IOException {
        write(Protocol.CP);
        write(path1);
        write(path2);
        if (reader.readLine().equals(Protocol.ERROR)) throw new IOException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pwd() throws IOException {
        write(Protocol.PWD);
        String s = reader.readLine();
        if (!s.equals(Protocol.ERROR)) dir = s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean touch(String name) throws IOException {
        write(Protocol.TOUCH);
        write(name);
        return reader.readLine().equals(Protocol.OK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean append(String fileName, String data) throws IOException {
        write(Protocol.APPEND);
        write(fileName);
        write(data);
        return reader.readLine().equals(Protocol.OK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invertHidden() {
        showHidden = !showHidden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(String path, String localPath) {

        NewFile nf = new NewFile(path, dir, localPath);
        TransferInfo ti = new TransferInfo(
                nf.getLocalPath() + "/" + nf.getName(),
                nf.getRemotePath() + "/" + nf.getName(),
                false
        );

        nf.setTi(ti);

        dataReceiveThread.addFile(nf);

        transferModel.addTransfer(ti);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String path, String localPath) {

        NewFile nf = new NewFile(path, dir, localPath);
        TransferInfo ti = new TransferInfo(
                nf.getLocalPath() + "/" + nf.getName(),
                nf.getRemotePath() + "/" + nf.getName(),
                true
        );

        nf.setTi(ti);

        dataSendThread.addFile(nf);

        transferModel.addTransfer(ti);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cd(String directory) {
        try {
            write(Protocol.CD);
            write(directory);

            if (reader.readLine().equals(Protocol.OK)) {
                String s = reader.readLine();
                if (s.equals(Protocol.ERROR)) {
                    new Alert("Błąd połączenia");
                } else {
                    dir = s;
                }
            }

        } catch (IOException e) {
            new Alert("Błąd połączenia");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean rm(String name) throws IOException {
        write(Protocol.RM);
        write(name);
        String s = reader.readLine();
        return s.equals(Protocol.OK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileInfo> listFiles() {
        try {
            List<FileInfo> list = new ArrayList<>();

            if (showHidden) {
                write(Protocol.LIST + " " + Protocol.TRUE);
            } else {
                write(Protocol.LIST + " " + Protocol.FALSE);
            }

            String s;
            while ((s = reader.readLine()) != null) {
                if (s.equals(Protocol.EOF)) {
                    break;
                }
                boolean directory = s.equals(Protocol.DIR);
                String name = reader.readLine();
                boolean hidden = reader.readLine().equals(Protocol.TRUE);
                long length = Long.parseLong(reader.readLine());
                list.add(new FileInfo(name, directory, hidden, length));
            }

            return list;

        } catch (IOException e) {
            new Alert("Błąd połączenia");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean login(String login, String pass) throws IOException {
        write(Protocol.USER + " " + login);

        if (reader.readLine().equals(Protocol.OK)) {
            write(Protocol.PASSWORD + " " + Base64Coder.encodeString(pass));
            return reader.readLine().equals(Protocol.OK);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connectPassive() throws IOException {

        write(Protocol.PASSIV);
        String s = reader.readLine();
        String[] args = s.split(" ");

        if (args[0].equals(Protocol.PORT) && args.length == 2) {
            int port = Integer.parseInt(args[1]);
            dataSocket = new Socket(controlSocket.getInetAddress(), port);
            boolean w = reader.readLine().equals(Protocol.OK);

            connectStreams();
            return w;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connectActive() throws IOException {
        int x;
        Random rand = new Random();
        do {
            x = rand.nextInt(Protocol.MAX_PORT_NUMBER);
        } while (!Main.isPortAvailable(x));

        ServerSocket serverSocket = new ServerSocket(x);
        write(Protocol.ACTIVE + " " + x);
        dataSocket = serverSocket.accept();
        serverSocket.setReuseAddress(true);
        serverSocket.close();

        boolean w = reader.readLine().equals(Protocol.OK);

        if (w) connectStreams();
        return w;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mkdir(String dir) throws IOException {
        write(Protocol.MKDIR);
        write(dir);
        String s = reader.readLine();
        return s.equals(Protocol.OK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mv(String oldFile, String newFile) throws IOException {
        write(Protocol.MV);
        write(oldFile);
        write(newFile);
        return reader.readLine().equals(Protocol.OK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        try {
            write(Protocol.EXIT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda zamykająca wszystkie połączenia w trakcie niszczenia obiektu
     *
     * @throws Throwable wyjątek
     */
    @Override
    protected void finalize() throws Throwable {

        if (reader != null)
            reader.close();

        if (writer != null)
            writer.close();

        if (controlSocket != null)
            controlSocket.close();

        if (dataSocket != null)
            dataSocket.close();

        if (dataSendThread != null)
            dataSendThread.setStop();

        if (dataReceiveThread != null)
            dataReceiveThread.setStop();

        super.finalize();
    }

    /**
     * Metoda ustawiająca typ transferu
     *
     * @param ascii true - tryb ASCII / false - tryb binarny
     * @return sukces
     * @throws IOException wyjątek
     */
    public boolean setTransfer(boolean ascii) throws IOException {
        this.ascii = ascii;
        if (ascii) {
            write(Protocol.TRANSFER + " " + Protocol.ASCII);
        } else {
            write(Protocol.TRANSFER + " " + Protocol.BINARY);
        }
        return reader.readLine().equals(Protocol.OK);
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
        writer.write(s);
        writer.newLine();
        writer.flush();
    }

    /**
     * Klasa wątku do wysyłania plików
     */
    class DataSendThread extends Thread {
        private boolean running = true;
        private BufferedWriter outASCII;
        private BufferedOutputStream out;
        private volatile List<NewFile> list;

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
        void addFile(NewFile f) {
            list.add(f);
        }

        @Override
        public void run() {
            while (running) {
                if (list.size() > 0) {

                    NewFile nf = list.remove(0);
                    File f = new File(nf.getLocalPath() + "/" + nf.getName());

                    try {
                        write(Protocol.PUT);
                        write(nf.getRemotePath() + "/" + nf.getName());

                        if (reader.readLine().equals(Protocol.OK)) {

                            if (ascii) {
                                sendASCII(f, nf.getTi());
                            } else {
                                sendBinary(f, nf.getTi());
                            }

                            transferModel.remove(
                                    nf.getLocalPath() + "/" + nf.getName(),
                                    nf.getRemotePath() + "/" + nf.getName());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    remoteModel.updateData();
                    remoteModel.fireTableDataChanged();
                }
            }
        }

        /**
         * Metoda wysyłająca plik w trybie ASCII
         *
         * @param f Plik do wysłania
         * @throws IOException wyjątek
         */
        private void sendASCII(File f, TransferInfo ti) throws IOException {
            BufferedReader buff =
                    new BufferedReader(
                            new FileReader(f));

            long size = f.length();
            long current = 0;
            send(Long.toString(size));

            String s;
            while ((s = buff.readLine()) != null) {
                transferModel.setProgress(ti, (int) (((double) current / size) * 100.0));
                current += s.length();
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
        private void sendBinary(File f, TransferInfo ti) throws IOException {

            BufferedInputStream buff =
                    new BufferedInputStream(new FileInputStream(f));

            int k;
            byte[] data = new byte[Protocol.PACKET_LENGTH];

            long max = f.length();
            long current = 0;
            send(ByteUtils.longToByte(max));

            while (current < max) {
                transferModel.setProgress(ti, (int) (((double) current / max) * 100.0));
                k = buff.read(data, 0, Protocol.PACKET_LENGTH);
                send(data, k);
                current += k;
            }

            buff.close();
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
                    File f = new File(nf.getLocalPath() + "/" + nf.getName());

                    try {
                        write(Protocol.GET);
                        write(nf.getRemotePath() + "/" + nf.getName());

                        if (reader.readLine().equals(Protocol.OK)) {

                            if (ascii) {
                                receiveASCII(f, nf.getTi());
                            } else {
                                receiveBinary(f, nf.getTi());
                            }

                            transferModel.remove(nf.getLocalPath() + "/" + nf.getName()
                                    , nf.getRemotePath() + "/" + nf.getName());

                        } else {
                            new Alert("Nie można pobrać pliku");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    localModel.updateData();
                    localModel.fireTableDataChanged();
                }
            }
        }

        /**
         * Metoda odbierająca plik w trybie ASCII
         *
         * @param f Plik do odebrania
         * @throws IOException wyjątek
         */
        private void receiveASCII(File f, TransferInfo ti) throws IOException {
            BufferedWriter buff =
                    new BufferedWriter(
                            new FileWriter(f));

            long size = Long.parseLong(inASCII.readLine());
            long current = 0;

            String s;
            while (!(s = inASCII.readLine()).equals(Protocol.EOF)) {
                transferModel.setProgress(ti, (int) (((double) current / size) * 100.0));
                if (s.equals("\\" + Protocol.EOF))
                    buff.write(s.substring(1, s.length()));
                else
                    buff.write(s);
                buff.newLine();
                buff.flush();
                current += s.length();
            }

            buff.close();
        }

        /**
         * Metoda odbierająca plik w trybie binarnym
         *
         * @param f Plik do odebrania
         * @throws IOException wyjątek
         */
        private void receiveBinary(File f, TransferInfo ti) throws IOException {
            BufferedOutputStream buff =
                    new BufferedOutputStream(
                            new FileOutputStream(f));

            int k;
            byte[] data = new byte[Protocol.PACKET_LENGTH];

            byte[] bytes = new byte[Long.BYTES];
            k = in.read(bytes);
            long max = ByteUtils.byteToLong(bytes);
            long current = 0;

            while (current < max) {
                transferModel.setProgress(ti, (int) (((double) current / max) * 100.0));
                k = in.read(data);
                buff.write(data, 0, k);
                current += k;
            }
            buff.flush();

            buff.close();
        }
    }
}
