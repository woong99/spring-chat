package potatowoong.domainwebsocket.config.socket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.WebSocketMessageBrokerStats

@Configuration
class WebSocketStatsConfig {


    @Bean
    fun customizeWebSocketStats(stats: WebSocketMessageBrokerStats): WebSocketMessageBrokerStats {
        stats.loggingPeriod = 5000 // 5초마다 로깅
        return stats
    }
}