package snacks.lang;

import static java.lang.Character.isJavaIdentifierStart;
import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.LinkedHashMap;
import java.util.Map;

public class JavaUtils {

    private static final Map<String, String> replacements;

    static {
        replacements = new LinkedHashMap<>();
        replacements.put("¢", "¢Jingle");
        replacements.put("$", "¢Bucks");
        replacements.put("?", "¢Query");
        replacements.put("!", "¢Bang");
        replacements.put("+", "¢Plus");
        replacements.put("-", "¢Dash");
        replacements.put("*", "¢Splat");
        replacements.put("/", "¢Slash");
        replacements.put("%", "¢Snort");
        replacements.put("&", "¢Amp");
        replacements.put("|", "¢Pipe");
        replacements.put("^", "¢Point");
        replacements.put("[]", "¢Sammich");
        replacements.put("..", "¢Dots");
        replacements.put("...", "¢Bore");
        replacements.put("=", "¢Same");
        replacements.put("<", "¢Left");
        replacements.put(">", "¢Right");
        replacements.put("~", "¢Wave");
        replacements.put("#snacks#~", "¢Snacks");
    }

    public static String javaClass(String module, String name) {
        return module + '.' + javaName(name);
    }

    public static String javaGetter(Symbol snacksName) {
        return javaGetter(snacksName.getValue());
    }

    public static String javaGetter(String snacksName) {
        return "get" + capitalize(javaName(snacksName));
    }

    public static String javaName(Symbol snacksName) {
        return javaName(snacksName.getValue());
    }

    public static String javaName(String snacksName) {
        String javaName = snacksName;
        for (String replacement : replacements.keySet()) {
            javaName = javaName.replace(replacement, replacements.get(replacement));
        }
        if (!isJavaIdentifierStart(javaName.charAt(0))) {
            javaName = "¢" + javaName;
        }
        return javaName;
    }
}
