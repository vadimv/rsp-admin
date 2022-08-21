package rsp.admin.pubsub;


import rsp.util.data.Tuple2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DefaultPubSub implements PubSub {

    private final Map<String, Tuple2<String, Consumer<String>>> handles = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String topic, String handleId, Consumer<String> handle) {
        System.out.println("Sub " + topic + " " + handleId );
        this.handles.put(handleId, new Tuple2<>(topic, handle));
    }

    @Override
    public void unsubscribe(String topic, String handleId) {
        System.out.println("UnSub " + topic + " " + handleId );
        this.handles.remove(handleId);
    }

    @Override
    public void publish(String topic, String value) {

        for (var handle : handles.values()) {
            if (topic.equals(handle._1)) {
                handle._2.accept(value);
                System.out.println("Pub " + topic + " " + value );
            }
        }
    }
}
