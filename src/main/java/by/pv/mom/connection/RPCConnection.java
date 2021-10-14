package by.pv.mom.connection;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.UnaryOperator;

import static by.pv.mom.util.Convertors.byteArrayToString;
import static by.pv.mom.util.Convertors.stringToByteArray;

public class RPCConnection implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;
    private final String queueName;

    private final Logger log = LoggerFactory.getLogger(RPCConnection.class);

    public RPCConnection(String queueName) throws IOException, TimeoutException {
        ConnectionFactory factory = ConnectionManager.newConnectionFactory();
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.queueName = queueName;
    }

    public String call(String message) throws Exception {
        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        return call(message, queue).take();
    }

    public RPCResponse call(String message, BlockingQueue<String> response) throws IOException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", queueName, props, stringToByteArray(message));

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(byteArrayToString(delivery.getBody()));
            }
        }, consumerTag -> {});

        return new RPCResponse(response, () -> cancelConsumer(ctag));
    }

    public String bindReceiver(UnaryOperator<String> requestTransformer, int processingTime, TimeUnit unit) {
        String receiverTag = "";
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queuePurge(queueName);
            channel.basicQos(1);

            log.info("[x] Awaiting RPC requests");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = byteArrayToString(delivery.getBody());
                    log.info("[.] message({})", message);
                    response += requestTransformer.apply(message);
                    Thread.sleep(unit.toMillis(processingTime));
                } catch (RuntimeException | InterruptedException e) {
                    log.error("[.]", e);
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, stringToByteArray(response));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            receiverTag = channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> {}));
        } catch (IOException e) {
            log.error("Binding error", e);
        }
        return receiverTag;
    }

    public String bindReceiver(UnaryOperator<String> requestTransformer) {
        return bindReceiver(requestTransformer, 0, TimeUnit.SECONDS);
    }



    public void unbindReceiver(String tag) throws IOException {
        channel.basicCancel(tag);
    }

    public void close() throws IOException {
        connection.close();
    }

    private Void cancelConsumer(String ctag) {
        try {
            channel.basicCancel(ctag);
        } catch (IOException e) {
            log.error("[.]", e);
        }
        return null;
    }
}
