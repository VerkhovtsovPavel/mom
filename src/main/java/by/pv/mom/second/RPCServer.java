package by.pv.mom.second;

import by.pv.mom.connection.RPCConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static by.pv.mom.util.Constant.RPC_QUEUE_NAME;

public class RPCServer {

    private static final Logger log = LoggerFactory.getLogger(RPCServer.class);

    public static void main(String[] argv) throws Exception {
        try (RPCConnection connection = new RPCConnection(RPC_QUEUE_NAME)) {
            connection.bindReceiver(String::toUpperCase);
            log.info("[*] To exit press any button");
            System.in.read();
        }
    }
}