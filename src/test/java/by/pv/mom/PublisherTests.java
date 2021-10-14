package by.pv.mom;

import by.pv.mom.connection.SimpleConnection;
import org.awaitility.core.ConditionTimeoutException;
import by.pv.mom.util.BlockingQueueCallback;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static by.pv.mom.util.Constant.EXCHANGE_NAME;
import static by.pv.mom.util.Constant.ROUTING_KEY;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublisherTests {

    private static SimpleConnection connection;

    @BeforeAll
    public static void setUp() throws IOException, TimeoutException {
        connection = new SimpleConnection(EXCHANGE_NAME);
    }

    @Test
    void shouldReceiveMessage() throws InterruptedException {
        final String originMessage = "Test Message";

        final BlockingQueueCallback response = new BlockingQueueCallback(1);
        connection.bindReceiver(ROUTING_KEY, response.getCallback());
        connection.send(ROUTING_KEY, originMessage);

        final String receivedMessage = response.getQueue().take();
        assertEquals(originMessage, receivedMessage);
    }

    @ParameterizedTest
    @CsvSource({"1,5", "1,3", "2,3"})
    void shouldReceiveMessageWithIntervalPositive(int delay, int expectedMessages) {
        final String originMessagePrefix = "Test Message:";
        final String originMessage = originMessagePrefix+ " %s";
        final BlockingQueueCallback response = new BlockingQueueCallback(expectedMessages);

        connection.bindReceiver(ROUTING_KEY, response.getCallback());
        ScheduledFuture sending = connection.scheduledSend(ROUTING_KEY, originMessage, delay, TimeUnit.SECONDS);

        await().atMost(delay*expectedMessages, TimeUnit.SECONDS).until(()-> response.getQueue().size() == expectedMessages);
        assertTrue(response.getQueue().stream()
                .allMatch(receivedMessage -> receivedMessage.startsWith(originMessagePrefix)));
        sending.cancel(true);
    }

    @Test
    void shouldReceiveMessageWithIntervalNegative() {
        final String originMessagePrefix = "Test Message:";
        final String originMessage = originMessagePrefix+ " %s";
        final long expectedSize = 3;
        final BlockingQueueCallback response = new BlockingQueueCallback(5);

        connection.bindReceiver(ROUTING_KEY, response.getCallback());
        ScheduledFuture sending = connection.scheduledSend(ROUTING_KEY, originMessage, 1, TimeUnit.SECONDS);

        assertThrows(ConditionTimeoutException.class, () ->
                await().atMost(expectedSize, TimeUnit.SECONDS).until(()-> response.getQueue().size() == 5)
        );

        sending.cancel(true);
        assertEquals(response.getQueue().size(), expectedSize);
    }

    @Test
    void shouldReceiveMessageInSecondConsumer() throws InterruptedException {
        final String originMessage = "Test Message";
        final BlockingQueueCallback firstConsumerResponse = new BlockingQueueCallback(1);
        final BlockingQueueCallback secondConsumerResponse = new BlockingQueueCallback(1);

        connection.bindReceiver(ROUTING_KEY, firstConsumerResponse.getCallback());
        connection.bindReceiver(ROUTING_KEY, secondConsumerResponse.getCallback());
        connection.send(ROUTING_KEY, originMessage);
        final String firstConsumerReceivedMessage = firstConsumerResponse.getQueue().take();
        final String secondConsumerReceivedMessage = secondConsumerResponse.getQueue().take();

        assertEquals(firstConsumerReceivedMessage, secondConsumerReceivedMessage);
        assertEquals(originMessage, firstConsumerReceivedMessage);
    }

    @AfterAll
    public static void termDown() throws IOException {
        connection.close();
    }
}
