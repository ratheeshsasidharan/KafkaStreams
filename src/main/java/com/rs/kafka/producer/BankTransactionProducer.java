package com.rs.kafka.producer;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BankTransactionProducer {
    private static Logger logger = LoggerFactory.getLogger(BankTransactionProducer.class);

    public static void main(String[] args) throws InterruptedException {
        KafkaProducer<String, String> kafkaProducer = getKafkaProducer();
        int i=1;
        Runtime.getRuntime().addShutdownHook(
                new Thread(kafkaProducer::close)
        );
        while(true){
            logger.info("Iteration : "+i);
            kafkaProducer.send(getProducerRecord("Thomas"));
            Thread.sleep(100);
            kafkaProducer.send(getProducerRecord("George"));
            Thread.sleep(100);
            kafkaProducer.send(getProducerRecord("Mathew"));
            Thread.sleep(100);
            i++;
        }
    }



    public static ProducerRecord<String,String> getProducerRecord(String name){
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        Integer amount = ThreadLocalRandom.current().nextInt(0,1000);
        Instant now = Instant.now();
        node.put("name",name);
        node.put("amount",amount);
        node.put("time",now.toString());
        return new ProducerRecord<String,String>("bank-transactions",name,node.toString());
    }


    private static KafkaProducer<String, String> getKafkaProducer() {
        Properties kafkaProp = new Properties();
        kafkaProp.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        kafkaProp.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProp.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        kafkaProp.setProperty(ProducerConfig.ACKS_CONFIG,"all");
        kafkaProp.setProperty(ProducerConfig.RETRIES_CONFIG,"3");
        kafkaProp.setProperty(ProducerConfig.LINGER_MS_CONFIG,"1");
        kafkaProp.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");

        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<String, String>(kafkaProp);
        return kafkaProducer;
    }
}
