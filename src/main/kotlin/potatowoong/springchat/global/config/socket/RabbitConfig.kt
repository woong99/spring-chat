package potatowoong.springchat.global.config.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableRabbit
class RabbitConfig(
    @Value("\${spring.rabbitmq.username}") private val username: String,
    @Value("\${spring.rabbitmq.password}") private val password: String,
    @Value("\${spring.rabbitmq.host}") private val host: String,
    @Value("\${spring.rabbitmq.port}") private val port: Int,
) {

    // Queue 등록
    @Bean
    fun queue() = Queue(CHAT_QUEUE_NAME, true)

    // Exchange 등록
    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(CHAT_EXCHANGE_NAME, true, false)
    }

    // Binding 등록
    @Bean
    fun binding(
        queue: Queue,
        exchange: TopicExchange
    ): Binding = BindingBuilder.bind(queue)
        .to(exchange)
        .with(ROUTING_KEY)

    // ConnectionFactory 등록
    @Bean
    fun connectionFactory(): ConnectionFactory {
        val factory = CachingConnectionFactory()
        factory.setHost(host)
        factory.username = username
        factory.setPassword(password)
        factory.port = port

        return factory
    }

    // RabbitTemplate 등록
    @Bean
    fun rabbitTemplate(): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory())
        rabbitTemplate.messageConverter = messageConverter()
        rabbitTemplate.routingKey = ROUTING_KEY

        return rabbitTemplate
    }

    // ListenerContainerFactory 등록
    @Bean
    fun simpleRabbitListenerContainerFactory(
        connectionFactory: ConnectionFactory
    ): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(messageConverter())

        return factory
    }

    // MessageConverter 등록
    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter {
        val objectMapper = ObjectMapper()
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        objectMapper.registerModule(dateTimeModule())

        return Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun dateTimeModule() = JavaTimeModule()

    companion object {
        private const val CHAT_QUEUE_NAME = "chat.queue"
        private const val CHAT_EXCHANGE_NAME = "chat.exchange"
        private const val ROUTING_KEY = "room.*"
    }
}
