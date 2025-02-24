package potatowoong.domainwebsocket.config.kafka

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
@EnableKafka
class KafkaConsumerConfig(
    @Value("\${spring.kafka.consumer.group-id}") private val groupId: String,
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String
) {

    private val log = KotlinLogging.logger { }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun consumerFactory(): DefaultKafkaConsumerFactory<String, String> {
        log.info { "Creating consumer factory with groupId: $groupId, bootstrapServers: $bootstrapServers" }
        val deserializer = JsonDeserializer(String::class.java)
        deserializer.addTrustedPackages("*")

        val errorHandlingValueDeserializer = ErrorHandlingDeserializer(deserializer)
        val consumerConfigurations = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to errorHandlingValueDeserializer,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to deserializer.javaClass.name,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest"
        )

        return DefaultKafkaConsumerFactory(consumerConfigurations, StringDeserializer(), errorHandlingValueDeserializer)
    }
}