package by.pv.mom.util;

import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static by.pv.mom.util.Convertors.byteArrayToString;

public class BlockingQueueCallback {

    final BlockingQueue<String> queue;
    final DeliverCallback callback;

    public BlockingQueueCallback(int capacity) {
        queue = new ArrayBlockingQueue<>(capacity);
        callback = (consumerTag, delivery) -> queue.offer(byteArrayToString(delivery.getBody()));
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public DeliverCallback getCallback() {
        return callback;
    }
}
