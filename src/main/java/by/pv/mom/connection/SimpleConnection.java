package by.pv.mom.connection;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static by.pv.mom.util.Convertors.stringToByteArray;
import static java.lang.String.format;

public class SimpleConnection implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;
    private final String exchangeName;

    private final Logger log = LoggerFactory.getLogger(SimpleConnection.class);

    public SimpleConnection(String exchangeName) throws IOException, TimeoutException {
        ConnectionFactory factory = ConnectionManager.newConnectionFactory();
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.exchangeName = exchangeName;
        this.channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
    }

    public void send(String routingKey, String message) {
        try {
            channel.basicPublish(exchangeName, routingKey, null, stringToByteArray(message));
            log.info("[x] Sent: '{}'", message);
        } catch (IOException e) {
            log.error("Sent error",e);
        }
    }

    public ScheduledFuture scheduledSend(String routingKey, String messagePattern, int delay, TimeUnit unit) {
        return new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(
                () -> this.send(routingKey, format(messagePattern,System.currentTimeMillis()))
                ,0, delay, unit);
    }

    public void bindReceiver(String routingKey, DeliverCallback deliverCallback) {
        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
            log.info("Bound callback to {}->{}->{}", queueName, routingKey, exchangeName);
        } catch (IOException e) {
            log.error("Binding error",e);
        }
    }


    public void close() throws IOException {
        connection.close();
    }
}