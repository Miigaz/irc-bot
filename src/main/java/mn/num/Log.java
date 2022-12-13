package mn.num;


import java.io.*;


public final class Log {
    protected static PrintStream logPrintStream = System.out;

    public static void setLog(PrintStream stream) {
        if (stream == null)
            throw new NullPointerException("null print stream argument");

        logPrintStream = stream;
    }

    public static void log(String msg) {

        logPrintStream.println(System.currentTimeMillis() + ": " + msg);
    }

    public static void debug(String msg) {

    }
}