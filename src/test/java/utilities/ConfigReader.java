package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream("src/main/java/config/config.properties"));
        } catch (IOException e) {
            System.out.println("Could not load config.properties, using defaults.");
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
} 