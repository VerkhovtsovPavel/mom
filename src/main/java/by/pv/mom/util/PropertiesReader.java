package by.pv.mom.util;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class PropertiesReader {
    private final static Properties properties = new Properties();

    public PropertiesReader() {
        try (InputStream input = new FileInputStream("src/main/resources/creds.properties")) {
            properties.load(input);
        } catch (IOException e) {
            Logger logger = LogManager.getRootLogger();
            logger.warn("creds.properties is not found");
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