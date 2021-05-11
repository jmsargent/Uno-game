package network;

import interfaces.ToJSON;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class CommandArguments extends HashMap<String, String> implements ToJSON {
    public CommandArguments() {}
    public CommandArguments (Map<String, String> args) {
        putAll(args);
    }

    /**
     * @param pairs key:value strings
     */
    public CommandArguments(String... pairs) {
        // Detta är så bigbrain , diggar det // aero
        for (String pair : pairs) {
            String[] parts = pair.split(":");
            put(parts[0], parts[1]);
        }
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public static String makePair(String key, String value) {
        return key+":"+value;
    }
}
