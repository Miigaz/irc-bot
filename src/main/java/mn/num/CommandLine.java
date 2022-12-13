package mn.num;

public class CommandLine {

    public static int getIntArg(String pname, String description, String[] args, int index) {
        if (index >= args.length) {
            System.err.println(pname + ": expected " + description + " integer argument following ");
            System.exit(1);
        }

        try {
            return (Integer.parseInt(args[index]));
        } catch (NumberFormatException e) {
            System.err.println(pname + ": Invalid " + description + " value " + args[index] + ": " + e.getMessage());
            System.exit(1);
            return (0);
        }
    }

    public static String getStringArg(String pname, String description, String[] args, int index) {

        if (index >= args.length) {
            System.err.println(pname + ": empty " + description + " argument");
            System.exit(1);
        }

        return (args[index]);
    }
}