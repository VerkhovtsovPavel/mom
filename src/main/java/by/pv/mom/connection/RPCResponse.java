package by.pv.mom.connection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;


public class RPCResponse {

    private final BlockingQueue<String> queue;
    private final Callable<Void> cancelConsumer;

    public RPCResponse(BlockingQueue<String> queue, Callable<Void> cancelConsumer) {
        this.queue = queue;
        this.cancelConsumer = cancelConsumer;
    }

    public String take() throws Exception {
       String result = queue.take();
       cancelConsumer.call();
       return result;
    }

}
