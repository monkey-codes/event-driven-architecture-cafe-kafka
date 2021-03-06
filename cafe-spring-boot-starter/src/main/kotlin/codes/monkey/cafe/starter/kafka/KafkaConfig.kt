package codes.monkey.cafe.starter.kafka

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import java.util.*


@EnableKafka
@Configuration
@EnableAutoConfiguration
class KafkaConfig {

    @Value(value = "\${kafka.bootstrapAddress}")
    private val bootstrapAddress: String? = null

    @Value(value = "\${kafka.schemaRegistryAddress}")
    private val schemaRegistryAddress: String? = null

    @Value(value = "\${kafka.producer.topic}")
    private val producerTopic: String? = null

    @Value(value = "\${kafka.consumer.group}")
    private val consumerGroup: String? = null

    @Bean
    fun kafkaAdmin(): KafkaAdmin? {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        configs[AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS] = false
        configs[AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION] =  true
        return KafkaAdmin(configs)
    }

    @Bean
    fun publicEvents(): NewTopic {
        return NewTopic(producerTopic!!, 1, 1.toShort())
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, SpecificRecordBase> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        configProps[AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS] = false
        configProps[AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION] =  true
        configProps[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryAddress!!
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, SpecificRecordBase> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, SpecificRecordBase> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup!!
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
        props[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryAddress!!
        props["specific.avro.reader"] = true
        props["auto.offset.reset"] =  "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, SpecificRecordBase> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, SpecificRecordBase>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.setErrorHandler(SeekToCurrentErrorHandler())
        return factory
    }

}