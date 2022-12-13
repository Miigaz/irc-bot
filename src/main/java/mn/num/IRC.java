package mn.num;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IRC {
    public IRC() {
    }

    public static void pass(String pass) {
        sendRawData("PASS " + pass);
    }

    public static void nick(String nick) {
        sendRawData("NICK " + nick);
    }

    public static void user(String nick, String hostname, String servername, String realname) {
        sendRawData("USER " + nick + " " + hostname + " " + servername + " " + realname);
    }

    public static void quit(String message) {
        sendRawData("QUIT :" + message);
    }

    public static void join(String channel) {
        sendRawData("JOIN " + channel);
    }

    public static void join(String channel, String password) {
        sendRawData("JOIN " + channel + " " + password);
    }

    public static void part(String channel) {
        sendRawData("PART " + channel);
    }

    public static void sendMessage(String channel, String message) {
        if (message.contains("tasklist")) {
            return;
        }
        sendRawData("PRIVMSG " + channel + " :" + message);
    }

    public static String getChannel(String line) {
        if (line.contains("#") && line.lastIndexOf(":") != -1 && line.contains("PRIVMSG") && line.indexOf("#") < line.lastIndexOf(" :"))
            return line.substring(line.indexOf("#"), line.lastIndexOf(" :")).replaceAll(" ", "");
        if (line.contains("PRIVMSG"))
            return line.substring(1, line.indexOf("!"));
        else
            return "null";
    }

    public static String getMessage(String line) {
        if (line.lastIndexOf(":") != -1)
            return line.substring(line.lastIndexOf(" :") + 2);
        else
            return null;
    }

    public static String getNick(String line) {
        try {
            return line.substring(1, line.indexOf("!"));
        } catch (Exception e) {
        }
        return null;
    }

    public static String readLine() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Main.getMainSock().getInputStream()));
            return reader.readLine();
        } catch (IOException ioe) {
            return "null";
        }
    }

    public static void sendRawData(String line) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Main.getMainSock().getOutputStream()));
            writer.write(line + "\n");
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}