package com.rs.kafka.streams.test;

import com.rs.kafka.producer.BankTransactionProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;

public class BankTransactionTest {

    @Test
    public void testRandomTransactions(){
        ProducerRecord<String,String> record = BankTransactionProducer.getProducerRecord("Jacob");
        Assert.assertEquals(record.key(),"Jacob");
    }
}
