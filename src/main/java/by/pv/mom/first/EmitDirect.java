package by.pv.mom.first;

import by.pv.mom.connection.SimpleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static by.pv.mom.util.Constant.EXCHANGE_NAME;
import static by.pv.mom.util.Constant.ROUTING_KEY;

public class EmitDirect {

    private static final Logger log = LoggerFactory.getLogger(EmitDirect.class);

    public static void main(String[] argv) throws Exception {
        try(SimpleConnection connection = new SimpleConnection(EXCHANGE_NAME)) {
            connection.scheduledSend(ROUTING_KEY, "Hello %s", 1, TimeUnit.SECONDS);
            log.info("[*] Sending messages. To exit press any button");
            System.in.read();
        }
    }
}

