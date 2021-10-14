package by.pv.mom.first;

import by.pv.mom.connection.SimpleConnection;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static by.pv.mom.util.Constant.EXCHANGE_NAME;
import static by.pv.mom.util.Constant.ROUTING_KEY;
import static by.pv.mom.util.Convertors.byteArrayToString;

public class ReceiveDirect {

    private static final Logger log = LoggerFactory.getLogger(ReceiveDirect.class);

    public static void main(String[] argv) throws Exception {
        try(SimpleConnection connection = new SimpleConnection(EXCHANGE_NAME)) {
            DeliverCallback deliverCallback = (consumerTag, delivery) -> log.info("[x] Received: '{}'", byteArrayToString(delivery.getBody()));
            connection.bindReceiver(ROUTING_KEY, deliverCallback);
            log.info("[*] Waiting for messages. To exit press any button");
            System.in.read();
        }
    }
}

