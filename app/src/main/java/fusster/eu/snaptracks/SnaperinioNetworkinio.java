package fusster.eu.snaptracks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ComBoro on 3/13/2017.
 */

public class SnaperinioNetworkinio {
    public interface SnaperinioListener {
        void onEvent(String data);
    }

    private static Thread networkThread = new Thread(null, getRunnable(), "Network Thread");

    private static final String HOST = "iordi.csgocrop.com";
    private static final int PORT = 25569;

    private static volatile Socket socket;
    private static volatile OutputStream out;

    private static List<SnaperinioListener> listeners = new ArrayList<>();

    public static void start() {
        if (networkThread.isAlive()) return;
        networkThread.setDaemon(true);
        networkThread.start();
    }

    public static OutputStream getOut() throws IOException {
        if (socket.isOutputShutdown()) throw new IOException("Output is shut down");
        return out;
    }

    public static boolean addListener(SnaperinioListener snaperinioListener) {
        return listeners.add(snaperinioListener);
    }

    public static boolean removeListener(SnaperinioListener snaperinioListener) {
        return listeners.remove(snaperinioListener);
    }

    private static Runnable getRunnable() {
        return () -> {
            try {
                socket = new Socket(InetAddress.getByName(HOST), PORT);
                SnaperinioNetworkinio.out = socket.getOutputStream();

                StringBuilder stringBuilder = new StringBuilder();

                InputStream in = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                int data = in.read();
                if (data == -1) {
                    // Connection closed
                } else {
                    stringBuilder.append((char) data);
                    stringBuilder.append(bufferedReader.read());
                    listeners.forEach(e -> e.onEvent(stringBuilder.toString()));
                    stringBuilder.setLength(0);
                }

            } catch (Exception e) {

            }
        };
    }

}
