package com.rs.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rs.kafka.model.JsonDeserializer;
import com.rs.kafka.model.JsonSerializer;
import com.rs.kafka.model.UserBankBalance;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;

public class BankBalanceStreamApp {

    public static void main(String[] args) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(StreamsConfig.APPLICATION_ID_CONFIG,"bank-balance-app");
        kafkaProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        kafkaProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProps.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG,"0");


        Serializer<UserBankBalance> serializer = new JsonSerializer<>();
        Deserializer<UserBankBalance> deSerializer = new JsonDeserializer<>();
        Serde<UserBankBalance> serde = Serdes.serdeFrom(serializer,deSerializer);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> stream = streamsBuilder.stream("bank-transactions");
        stream.groupByKey(Serialized.with(Serdes.String(),Serdes.String()))
                .aggregate(() -> new UserBankBalance(),(key, value, userBankBalance) -> {
            userBankBalance.setUserName(key);
            JsonObject jsonObject = new Gson().fromJson(value, JsonObject.class);
            userBankBalance.setBalance(userBankBalance.getBalance()+jsonObject.get("amount").getAsInt());
            userBankBalance.setLastUpdateTime(jsonObject.get("time").getAsString());
            return userBankBalance;
            }, Materialized.as("bank-balance-agg").with(Serdes.String(),serde)).mapValues((key, userBankBalance) -> {
                return new Gson().toJson(userBankBalance);
            }).toStream()
            .to("bank-balance", Produced.with(Serdes.String(), Serdes.String()));
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(),kafkaProps);
        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(
                new Thread(streams::close)
        );
    }


}

