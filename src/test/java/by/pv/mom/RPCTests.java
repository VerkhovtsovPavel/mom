package by.pv.mom;

import by.pv.mom.connection.RPCConnection;
import by.pv.mom.connection.RPCResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static by.pv.mom.util.Constant.RPC_QUEUE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RPCTests {

    private static RPCConnection connection;

    @BeforeAll
    public static void setUp() throws IOException, TimeoutException {
        connection = new RPCConnection(RPC_QUEUE_NAME);
        connection.bindReceiver(String::toUpperCase);
    }

    @Test
    void shouldReceiveMessageInUpperCase() throws Exception {
        final String originMessage = "Test Message";

        String response = connection.call(originMessage);

        assertEquals(originMessage.toUpperCase(), response);
    }

    @Test
    void shouldReceiveProperMessageInCaseOfMultipleCall() throws Exception {
        final String firstMessage = "Test Message";
        final String secondMessage = "Message Test";
        final BlockingQueue<String> firstQueue = new ArrayBlockingQueue<>(1);
        final BlockingQueue<String> secondQueue = new ArrayBlockingQueue<>(1);

        RPCResponse firstResponse = connection.call(firstMessage, firstQueue);
        RPCResponse secondResponse = connection.call(secondMessage, secondQueue);

        assertEquals(firstMessage.toUpperCase(), firstResponse.take());
        assertEquals(secondMessage.toUpperCase(), secondResponse.take());
    }

    @Test
    void shouldReceiveMessageInCaseMultipleServers() throws Exception {
        String tag = connection.bindReceiver(String::toLowerCase);
        final String originMessage = "Test Message";

        String upperResponse = connection.call(originMessage);
        String lowerResponse = connection.call(originMessage);

        assertEquals(originMessage.toUpperCase(), upperResponse);
        assertEquals(originMessage.toLowerCase(), lowerResponse);

        connection.unbindReceiver(tag);
    }

    @AfterAll
    public static void termDown() throws IOException {
        connection.close();
    }
}