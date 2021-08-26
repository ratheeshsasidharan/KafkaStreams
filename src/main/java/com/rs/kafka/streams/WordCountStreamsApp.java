package com.rs.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

public class WordCountStreamsApp {

    public static void main(String[] args) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(StreamsConfig.APPLICATION_ID_CONFIG,"word-count-app");
        kafkaProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        kafkaProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> stream = streamsBuilder.stream("streams-plaintext-input");
        KTable<String,Long> wordCount = stream.mapValues(value -> value.toLowerCase())
                .flatMapValues(value -> Arrays.asList(value.split(" ")))
                .selectKey((key, val) -> val)
                .groupByKey()
                .count();
        wordCount.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(),kafkaProps);
        streams.start();

        System.out.println(streams.toString());

        Runtime.getRuntime().addShutdownHook(
                new Thread(streams::close)
        );
    }
}
