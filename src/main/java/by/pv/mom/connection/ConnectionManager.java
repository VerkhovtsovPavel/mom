package by.pv.mom.connection;

import by.pv.mom.util.PropertiesReader;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionManager {

    private ConnectionManager() {}

    public static ConnectionFactory newConnectionFactory() {
        PropertiesReader propertiesReader = new PropertiesReader();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(propertiesReader.getHost());
        factory.setVirtualHost(propertiesReader.getVirtualHost());
        factory.setUsername(propertiesReader.getUsername());
        factory.setPassword(propertiesReader.getPassword());
        return factory;
    }
}
