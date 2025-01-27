package potatowoong.springchat.global.config.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.amqp.core.*
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
    @Value("\${notification.queue-number}") private val notificationQueueNumber: Int
) {

    // Chat Queue 등록
    @Bean
    fun chatQueue() = Queue(CHAT_QUEUE_NAME, true)

    // Notification Queue 등록
    @Bean
    fun notificationQueue() = Queue("$NOTIFICATION_QUEUE_NAME.$notificationQueueNumber", true)

    // Chat Exchange 등록
    @Bean
    fun chatExchange(): TopicExchange {
        return TopicExchange(CHAT_EXCHANGE_NAME, true, false)
    }

    // Notification Exchange 등록
    @Bean
    fun notificationExchange(): DirectExchange {
        return DirectExchange(NOTIFICATION_EXCHANGE_NAME, true, false)
    }

    // Chat Binding 등록
    @Bean
    fun chatBinding(
        chatQueue: Queue,
        chatExchange: TopicExchange
    ): Binding = BindingBuilder.bind(chatQueue)
        .to(chatExchange)
        .with(CHAT_ROUTING_KEY)

    // Notification Binding 등록
    @Bean
    fun notificationBinding(
        notificationQueue: Queue,
        notificationExchange: DirectExchange
    ): Binding = BindingBuilder.bind(notificationQueue)
        .to(notificationExchange)
        .with(NOTIFICATION_ROUTING_KEY)

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
        rabbitTemplate.routingKey = CHAT_ROUTING_KEY

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
        private const val CHAT_ROUTING_KEY = "room.*"
        private const val NOTIFICATION_EXCHANGE_NAME = "notification.exchange"
        private const val NOTIFICATION_QUEUE_NAME = "notification.queue"
        private const val NOTIFICATION_ROUTING_KEY = "notification"
    }
}
