package by.pv.mom;

import by.pv.mom.connection.RPCConnection;
import by.pv.mom.connection.RPCResponse;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static by.pv.mom.util.Constant.RPC_QUEUE_NAME;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RPCProcessingTimeTests {

    private static RPCConnection connection;

    @BeforeAll
    public static void setUp() throws IOException, TimeoutException {
        connection = new RPCConnection(RPC_QUEUE_NAME);
    }

    @Test
    void shouldReceiveResponseWithReasonableTimeoutPositive() throws Exception {
        String tag = connection.bindReceiver(String::toUpperCase, 2, TimeUnit.SECONDS);
        final String originMessage = "Test Message";
        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        RPCResponse response = connection.call(originMessage, queue);

        await().atMost(3, TimeUnit.SECONDS).until(()-> !queue.isEmpty());
        assertEquals(originMessage.toUpperCase(), response.take());

        connection.unbindReceiver(tag);
    }

    @Test
    void shouldReceiveResponseWithReasonableTimeoutNegative() throws Exception {
        String tag = connection.bindReceiver(String::toUpperCase, 5, TimeUnit.SECONDS);
        final String originMessage = "Test Message";
        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        connection.call(originMessage, queue);

        assertThrows(ConditionTimeoutException.class, () ->
                await().atMost(3, TimeUnit.SECONDS).until(()-> !queue.isEmpty())
        );

        connection.unbindReceiver(tag);
    }

    @AfterAll
    public static void termDown() throws IOException {
        connection.close();
    }
}