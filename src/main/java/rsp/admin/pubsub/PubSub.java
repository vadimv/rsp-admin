package rsp.admin.pubsub;


import rsp.util.data.Tuple2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public interface PubSub {

    void subscribe(String topic, String handleId, Consumer<String> handle);

    void unsubscribe(String topic, String handleId);

   void publish(String topic, String value);
}
