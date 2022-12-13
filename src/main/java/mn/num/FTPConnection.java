package mn.num;

import java.io.*;
import java.net.*;
import java.util.*;

public class FTPConnection extends Object {
    private static boolean PRINT_DEBUG_INFO = false;
    private Socket connectionSocket = null;
    private PrintStream outputStream = null;
    private BufferedReader inputStream = null;
    private long restartPoint = 0L;
    private boolean loggedIn = false;
    public String lineTerm = "\n";
    private static int BLOCK_SIZE = 4096;
    public FTPConnection() {

    }

    public FTPConnection(boolean debugOut) {
        PRINT_DEBUG_INFO = debugOut;
    }
    private void debugPrint(String message) {
        if (PRINT_DEBUG_INFO) System.err.println(message);
    }

    public boolean connect(String host) throws UnknownHostException, IOException {
        return connect(host, 21);
    }


    public boolean connect(String host, int port) throws UnknownHostException, IOException {
        connectionSocket = new Socket(host, port);
        outputStream = new PrintStream(connectionSocket.getOutputStream());
        inputStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        if (!isPositiveCompleteResponse(getServerReply())) {
            disconnect();
            return false;
        }

        return true;
    }


    public void disconnect() {
        if (outputStream != null) {
            try {
                if (loggedIn) {
                    logout();
                }
                ;
                outputStream.close();
                inputStream.close();
                connectionSocket.close();
            } catch (IOException e) {
            }

            outputStream = null;
            inputStream = null;
            connectionSocket = null;
        }
    }


    public boolean login(String username, String password) throws IOException {
        int response = executeCommand("user " + username);
        if (!isPositiveIntermediateResponse(response)) return false;
        response = executeCommand("pass " + password);
        loggedIn = isPositiveCompleteResponse(response);
        return loggedIn;
    }


    public boolean logout() throws IOException {
        int response = executeCommand("quit");
        loggedIn = !isPositiveCompleteResponse(response);
        return !loggedIn;
    }


    public boolean changeDirectory(String directory) throws IOException {
        int response = executeCommand("cwd " + directory);
        return isPositiveCompleteResponse(response);
    }


    public boolean renameFile(String oldName, String newName) throws IOException {
        int response = executeCommand("rnfr " + oldName);
        if (!isPositiveIntermediateResponse(response)) return false;
        response = executeCommand("rnto " + newName);
        return isPositiveCompleteResponse(response);
    }


    public boolean makeDirectory(String directory) throws IOException {
        int response = executeCommand("mkd " + directory);
        return isPositiveCompleteResponse(response);
    }


    public boolean removeDirectory(String directory) throws IOException {
        int response = executeCommand("rmd " + directory);
        return isPositiveCompleteResponse(response);
    }


    public boolean parentDirectory() throws IOException {
        int response = executeCommand("cdup");
        return isPositiveCompleteResponse(response);
    }


    public boolean deleteFile(String fileName) throws IOException {
        int response = executeCommand("dele " + fileName);
        return isPositiveCompleteResponse(response);
    }


    public String getCurrentDirectory() throws IOException {
        String response = getExecutionResponse("pwd");
        StringTokenizer strtok = new StringTokenizer(response);


        if (strtok.countTokens() < 2) return null;
        strtok.nextToken();
        String directoryName = strtok.nextToken();


        int strlen = directoryName.length();
        if (strlen == 0) return null;
        if (directoryName.charAt(0) == '\"') {
            directoryName = directoryName.substring(1);
            strlen--;
        }
        if (directoryName.charAt(strlen - 1) == '\"') return directoryName.substring(0, strlen - 1);
        return directoryName;
    }


    public String getSystemType() throws IOException {
        return excludeCode(getExecutionResponse("syst"));
    }


    public long getModificationTime(String fileName) throws IOException {
        String response = excludeCode(getExecutionResponse("mdtm " + fileName));
        try {
            return Long.parseLong(response);
        } catch (Exception e) {
            return -1L;
        }
    }


    public long getFileSize(String fileName) throws IOException {
        String response = excludeCode(getExecutionResponse("size " + fileName));
        try {
            return Long.parseLong(response);
        } catch (Exception e) {
            return -1L;
        }
    }


    public boolean downloadFile(String fileName) throws IOException {
        return readDataToFile("retr " + fileName, fileName);
    }


    public boolean downloadFile(String serverPath, String localPath) throws IOException {
        return readDataToFile("retr " + serverPath, localPath);
    }


    public boolean uploadFile(String fileName) throws IOException {
        return writeDataFromFile("stor " + fileName, fileName);
    }


    public boolean uploadFile(String serverPath, String localPath) throws IOException {
        return writeDataFromFile("stor " + serverPath, localPath);
    }


    public void setRestartPoint(int point) {
        restartPoint = point;
        debugPrint("Restart noted");
    }


    private int getServerReply() throws IOException {
        return Integer.parseInt(getFullServerReply().substring(0, 3));
    }


    private String getFullServerReply() throws IOException {
        String reply;
        do {
            reply = inputStream.readLine();
            debugPrint(reply);
        } while (!(Character.isDigit(reply.charAt(0)) && Character.isDigit(reply.charAt(1)) && Character.isDigit(reply.charAt(2)) && reply.charAt(3) == ' '));

        return reply;
    }


    @SuppressWarnings("unused")
    private String getFullServerReply(StringBuffer fullReply) throws IOException {
        String reply;
        fullReply.setLength(0);

        do {
            reply = inputStream.readLine();
            debugPrint(reply);
            fullReply.append(reply + lineTerm);
        } while (!(Character.isDigit(reply.charAt(0)) && Character.isDigit(reply.charAt(1)) && Character.isDigit(reply.charAt(2)) && reply.charAt(3) == ' '));

        if (fullReply.length() > 0) {
            fullReply.setLength(fullReply.length() - lineTerm.length());
        }

        return reply;
    }


    public String listFiles() throws IOException {
        return listFiles("");
    }


    public String listFiles(String params) throws IOException {
        StringBuffer files = new StringBuffer();
        StringBuffer dirs = new StringBuffer();
        if (!getAndParseDirList(params, files, dirs)) {
            debugPrint("Error getting file list");
        }

        return files.toString();
    }


    public String listSubdirectories() throws IOException {
        return listSubdirectories("");
    }


    public String listSubdirectories(String params) throws IOException {
        StringBuffer files = new StringBuffer();
        StringBuffer dirs = new StringBuffer();
        if (!getAndParseDirList(params, files, dirs)) {
            debugPrint("Error getting dir list");
        }

        return dirs.toString();
    }


    private String processFileListCommand(String command) throws IOException {
        StringBuffer reply = new StringBuffer();
        String replyString;

        boolean success = executeDataCommand(command, reply);
        if (!success) {
            return "";
        }

        replyString = reply.toString();

        if (reply.length() > 0) {
            return replyString.substring(0, reply.length() - 1);
        } else {
            return replyString;
        }
    }


    private boolean getAndParseDirList(String params, StringBuffer files, StringBuffer dirs) throws IOException {
        files.setLength(0);
        dirs.setLength(0);

        String shortList = processFileListCommand("nlst " + params);
        String longList = processFileListCommand("list " + params);

        StringTokenizer sList = new StringTokenizer(shortList, "\n");
        StringTokenizer lList = new StringTokenizer(longList, "\n");

        String sString;
        String lString;

        while ((sList.hasMoreTokens()) && (lList.hasMoreTokens())) {
            sString = sList.nextToken();
            lString = lList.nextToken();

            if (lString.length() > 0) {
                if (lString.startsWith("d")) {
                    dirs.append(sString.trim() + lineTerm);
                    debugPrint("Dir: " + sString);
                } else if (lString.startsWith("-")) {
                    files.append(sString.trim() + lineTerm);
                    debugPrint("File: " + sString);
                } else {
                    debugPrint("Unknown: " + lString);
                }
            }
        }

        if (files.length() > 0) {
            files.setLength(files.length() - lineTerm.length());
        }
        if (dirs.length() > 0) {
            dirs.setLength(dirs.length() - lineTerm.length());
        }

        return true;
    }

    public int executeCommand(String command) throws IOException {
        outputStream.println(command);
        return getServerReply();
    }

    public String getExecutionResponse(String command) throws IOException {
        outputStream.println(command);
        return getFullServerReply();
    }

    public boolean readDataToFile(String command, String fileName) throws IOException {
        RandomAccessFile outfile = new RandomAccessFile(fileName, "rw");
        if (restartPoint != 0) {
            debugPrint("Seeking to " + restartPoint);
            outfile.seek(restartPoint);
        }

        FileOutputStream fileStream = new FileOutputStream(outfile.getFD());
        boolean success = executeDataCommand(command, fileStream);

        outfile.close();
        return success;
    }

    public boolean writeDataFromFile(String command, String fileName) throws IOException {
        RandomAccessFile infile = new RandomAccessFile(fileName, "r");
        if (restartPoint != 0) {
            debugPrint("Seeking to " + restartPoint);
            infile.seek(restartPoint);
        }

        FileInputStream fileStream = new FileInputStream(infile.getFD());
        boolean success = executeDataCommand(command, fileStream);

        infile.close();
        return success;
    }

    public boolean executeDataCommand(String command, OutputStream out) throws IOException {

        ServerSocket serverSocket = new ServerSocket(0);
        if (!setupDataPort(command, serverSocket)) return false;
        Socket clientSocket = serverSocket.accept();

        InputStream in = clientSocket.getInputStream();
        transferData(in, out);

        in.close();
        clientSocket.close();
        serverSocket.close();

        return isPositiveCompleteResponse(getServerReply());
    }

    public boolean executeDataCommand(String command, InputStream in) throws IOException {

        ServerSocket serverSocket = new ServerSocket(0);
        if (!setupDataPort(command, serverSocket)) return false;
        Socket clientSocket = serverSocket.accept();

        OutputStream out = clientSocket.getOutputStream();
        transferData(in, out);

        out.close();
        clientSocket.close();
        serverSocket.close();

        return isPositiveCompleteResponse(getServerReply());
    }


    public boolean executeDataCommand(String command, StringBuffer sb) throws IOException {

        ServerSocket serverSocket = new ServerSocket(0);
        if (!setupDataPort(command, serverSocket)) return false;
        Socket clientSocket = serverSocket.accept();

        InputStream in = clientSocket.getInputStream();
        transferData(in, sb);

        in.close();
        clientSocket.close();
        serverSocket.close();

        return isPositiveCompleteResponse(getServerReply());
    }


    private void transferData(InputStream in, OutputStream out) throws IOException {
        byte b[] = new byte[BLOCK_SIZE];
        int amount;

        while ((amount = in.read(b)) > 0) {
            out.write(b, 0, amount);
        }
    }


    private void transferData(InputStream in, StringBuffer sb) throws IOException {
        byte b[] = new byte[BLOCK_SIZE];
        int amount;

        while ((amount = in.read(b)) > 0) {
            sb.append(new String(b, 0, amount));
        }
    }


    private boolean setupDataPort(String command, ServerSocket serverSocket) throws IOException {
        if (!openPort(serverSocket)) return false;

        outputStream.println("type i");
        if (!isPositiveCompleteResponse(getServerReply())) {
            debugPrint("Could not set transfer type");
            return false;
        }

        if (restartPoint != 0) {
            outputStream.println("rest " + restartPoint);
            restartPoint = 0;

            getServerReply();
        }

        outputStream.println(command);
        return isPositivePreliminaryResponse(getServerReply());
    }

    @SuppressWarnings("static-access")
    private boolean openPort(ServerSocket serverSocket) throws IOException {
        int localport = serverSocket.getLocalPort();

        InetAddress inetaddress = serverSocket.getInetAddress();
        InetAddress localip;
        try {
            localip = inetaddress.getLocalHost();
        } catch (UnknownHostException e) {
            debugPrint("Can't get local host");
            return false;
        }

        byte[] addrbytes = localip.getAddress();
        short[] addrshorts = new short[4];

        for (int i = 0; i <= 3; i++) {
            addrshorts[i] = addrbytes[i];
            if (addrshorts[i] < 0) addrshorts[i] += 256;
        }

        outputStream.println("port " + addrshorts[0] + "," + addrshorts[1] + "," + addrshorts[2] + "," + addrshorts[3] + "," + ((localport & 0xff00) >> 8) + "," + (localport & 0x00ff));
        return isPositiveCompleteResponse(getServerReply());
    }


    private boolean isPositivePreliminaryResponse(int response) {
        return (response >= 100 && response < 200);
    }
    private boolean isPositiveIntermediateResponse(int response) {
        return (response >= 300 && response < 400);
    }
    private boolean isPositiveCompleteResponse(int response) {
        return (response >= 200 && response < 300);
    }
    @SuppressWarnings("unused")
    private boolean isTransientNegativeResponse(int response) {
        return (response >= 400 && response < 500);
    }
    @SuppressWarnings("unused")
    private boolean isPermanentNegativeResponse(int response) {
        return (response >= 500 && response < 600);
    }
    private String excludeCode(String response) {
        if (response.length() < 5) return response;
        return response.substring(4);
    }

}
