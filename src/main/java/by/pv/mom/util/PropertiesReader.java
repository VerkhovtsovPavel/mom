package by.pv.mom.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final Properties properties = new Properties();

    public PropertiesReader() {
        if (properties.isEmpty()) {
            try (InputStream input = new FileInputStream("src/main/resources/creds.properties")) {
                properties.load(input);
            } catch (IOException e) {
                Logger logger = LogManager.getRootLogger();
                logger.warn("creds.properties is not found");
            }
        }
    }

    public String getUsername() {
        return getProperty("username");
    }

    public String getPassword() {
        return getProperty("password");
    }

    public String getHost() {
        return getProperty("host");
    }

    public String getVirtualHost() {
        return getProperty("virtual-host");
    }

    private String getProperty(String key) {
        return properties.getProperty(key);
    }
}