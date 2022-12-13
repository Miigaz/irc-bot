package mn.num;

import java.util.Random;

public class NickGen {

    public NickGen() {
    }
    public static String getUniqueID() {
        String str = new String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int te = 0;
        for (int i = 1; i <= 4; i++) {
            te = r.nextInt(62);
            sb.append(str.charAt(te));
        }
        return sb.toString();
    }
}