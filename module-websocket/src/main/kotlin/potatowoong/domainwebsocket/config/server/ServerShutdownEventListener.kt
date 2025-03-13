package potatowoong.domainwebsocket.config.server

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextClosedEvent
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.config.WebSocketMessageBrokerStats
import potatowoong.domainwebsocket.chat.dto.MessageDto
import java.net.NetworkInterface
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
@Profile("prod")
class ServerShutdownEventListener(
    @Value("\${load-balancer-url}")
    private val loadBalancerUrl: String,
    @Value("\${deploy}")
    private val deploy: String,
    private val restTemplate: RestTemplate,
    private val messagingTemplate: SimpMessagingTemplate,
    private val webSocketMessageBrokerStats: WebSocketMessageBrokerStats
) : ApplicationListener<ContextClosedEvent> {

    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ContextClosedEvent) {
        // 컨테이너 IP 획득
        val ip = NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .first { it.isSiteLocalAddress }
            .hostAddress

        // 로드밸런서에 서버 Down 상태 전달
        restTemplate.getForObject("$loadBalancerUrl/ws-down?ip=$ip&deploy=$deploy", String::class.java)

        // 모든 웹소켓 세션 종료
        messagingTemplate.convertAndSend("/sub/global", MessageDto.Request.ofClose())

        // 모든 웹소켓 세션 종료 대기
        val latch = CountDownLatch(1)
        val scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate({
            log.info { "WebSocketStats : ${webSocketMessageBrokerStats.webSocketSessionStats}" }

            if (webSocketMessageBrokerStats.webSocketSessionStats?.totalSessions == 0) {
                latch.countDown()
            }
        }, 0, 1, TimeUnit.SECONDS)
    }
}