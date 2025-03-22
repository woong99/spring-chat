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
import org.springframework.web.util.UriComponentsBuilder
import potatowoong.domainwebsocket.chat.dto.MessageDto
import potatowoong.domainwebsocket.common.utils.IpUtils
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
@Profile("dev")
class ServerShutdownEventListener(
    @Value("\${load-balancer-url}") private val loadBalancerUrl: String,
    @Value("\${deploy}") private val deploy: String,
    private val restTemplate: RestTemplate,
    private val messagingTemplate: SimpMessagingTemplate,
    private val webSocketMessageBrokerStats: WebSocketMessageBrokerStats
) : ApplicationListener<ContextClosedEvent> {

    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ContextClosedEvent) {
        // 로드밸런서에 서버 Down 상태 전달
        notifyLoadBalancer()

        // 모든 웹소켓 세션 종료
        closeWebSocketSession()
    }

    private fun notifyLoadBalancer() {
        // 컨테이너 IP 획득
        val ip = IpUtils.getLocalIp()

        // 로드밸런서에 서버 Down 상태 전달
        val uri = UriComponentsBuilder.fromUriString("$loadBalancerUrl/ws-down")
            .queryParam("ip", ip)
            .queryParam("deploy", deploy)
            .build()
            .toUriString()
        restTemplate.getForObject(uri, String::class.java)
    }

    private fun closeWebSocketSession() {
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