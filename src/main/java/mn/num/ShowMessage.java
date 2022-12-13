package mn.num;

import javax.swing.JOptionPane;

public class ShowMessage extends Thread {
    String message;
    public ShowMessage(String mess) {
        message = mess;
    }

    public void run() {
        JOptionPane.showMessageDialog(null, message, "Message", -1);
    }
}