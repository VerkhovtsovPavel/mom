package by.pv.mom.rpc;

import by.pv.mom.connection.RPCConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static by.pv.mom.util.Constant.RPC_QUEUE_NAME;

public class RPCClient {

    private static final Logger log = LoggerFactory.getLogger(RPCClient.class);

    public static void main(String[] argv) {
        try (RPCConnection connection = new RPCConnection(RPC_QUEUE_NAME)) {
            for (int i = 0; i < 32; i++) {
                String message = "Message -> " + i;
                log.info("[>] Requesting {}",message);
                String response = connection.call(message);
                log.info("[<] Receiving '{}'", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

