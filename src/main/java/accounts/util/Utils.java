package accounts.util;

public class Utils {

    public static void sleep(long secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (Exception e) {
            // do nothing
        }
    }
}
