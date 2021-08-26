# Project for learning streamers in Kafka

##Setup

###Start Zoo Keeper
zookeeper-server-start.bat config/zookeeper.properties

###Start Kafka
kafka-server-start.bat config/server.properties

###Create topics

kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic streams-plaintext-input --create --partitions 3 --replication-factor 1
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic streams-plaintext-output --create --partitions 3 --replication-factor 1
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic word-count-output --create --partitions 3 --replication-factor 1


###List topics 
kafka-topics.bat --zookeeper 127.0.0.1:2181 --list

###Producer
kafka-console-producer --broker-list 127.0.0.1:9092 --topic streams-plaintext-input


###Consumer
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic streams-plaintext-input --from-beginning
####print key and value after deserialising
kafka-console-consumer --bootstrap-server localhost:9092 --topic streams-wordcount-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter -property print.value=true --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
kafka-console-consumer --bootstrap-server localhost:9092 --topic word-count-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter -property print.value=true --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

###Start demo streamer
kafka-run-class.bat org.apache.kafka.streams.examples.wordcount.WordCountDemo


###Topic for ColorCountStreamsApp
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic color-count-input --create --partitions 3 --replication-factor 1
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic user-color --create --partitions 3 --replication-factor 1 --config cleanup.policy=compact
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic color-count-output --create --partitions 3 --replication-factor 1 --config cleanup.policy=compact
kafka-console-consumer --bootstrap-server localhost:9092 --topic user-color --from-beginning --formatter kafka.tools.DefaultMessageFormatter -property print.value=true --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

kafka-console-consumer --bootstrap-server localhost:9092 --topic color-count-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter -property print.value=true --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer


###Topic for Bank Balance Calculator
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic bank-transactions --create --partitions 3 --replication-factor 1
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic bank-balance --create --partitions 3 --replication-factor 1

kafka-console-consumer --bootstrap-server localhost:9092 --topic bank-transactions --from-beginning --formatter kafka.tools.DefaultMessageFormatter -property print.value=true --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
kafka-console-consumer --bootstrap-server localhost:9092 --topic bank-balance --from-beginning --formatter kafka.tools.DefaultMessageFormatter -property print.value=true --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

