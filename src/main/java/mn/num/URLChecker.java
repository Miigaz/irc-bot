package mn.num;

import java.net.URL;
import java.net.HttpURLConnection;

public class URLChecker {

    private static URL url;

    public URLChecker(URL url) {
        URLChecker.url = url;
    }

    public static boolean check() {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return (conn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

}