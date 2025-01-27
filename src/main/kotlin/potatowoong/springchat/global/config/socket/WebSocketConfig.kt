package potatowoong.springchat.global.config.socket

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.util.AntPathMatcher
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val stompInterceptor: StompInterceptor,
    private val stompErrorHandler: StompErrorHandler,
    @Value("\${spring.rabbitmq.username}") private val username: String,
    @Value("\${spring.rabbitmq.password}") private val password: String,
    @Value("\${spring.rabbitmq.host}") private val host: String,
    @Value("\${spring.rabbitmq.stomp-port}") private val stompPort: Int,
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // 메시지 구독 설정
        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
            .setAutoStartup(true)
            .setClientLogin(username)
            .setClientPasscode(password)
            .setSystemLogin(username)
            .setSystemPasscode(password)
            .setRelayHost(host)
            .setRelayPort(stompPort)

        // 메시지 발행 설정
        registry.setPathMatcher(AntPathMatcher("."))
        registry.setApplicationDestinationPrefixes("/pub")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws-stomp")
            .setAllowedOriginPatterns("*") // TODO : 배포 시 변경
            .withSockJS()

        registry.addEndpoint("/ws-stomp")
            .setAllowedOriginPatterns("*") // TODO : 배포 시 변경

        registry.setErrorHandler(stompErrorHandler)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompInterceptor)
    }
}
