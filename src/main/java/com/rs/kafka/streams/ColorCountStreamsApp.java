package com.rs.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class ColorCountStreamsApp {

    public static void main(String[] args) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(StreamsConfig.APPLICATION_ID_CONFIG,"color-count-app");
        kafkaProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        kafkaProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProps.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG,"0");

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> stream = streamsBuilder.stream("color-count-input");
        stream.filter((key, val) -> val.contains(","))
            .map((key, val) -> new KeyValue<>(val.split(",")[0],val.split(",")[1]))
            .to("user-color", Produced.with(Serdes.String(), Serdes.String()));

        KTable<String, String> userColorStream = streamsBuilder.table("user-color");
        KTable<String,Long> colorCount = userColorStream.groupBy((key, val) -> new KeyValue<>(val,val))
                .count();
        colorCount.toStream().to("color-count-output", Produced.with(Serdes.String(), Serdes.Long()));


        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(),kafkaProps);
        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(
                new Thread(streams::close)
        );
    }
}
