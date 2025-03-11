package potatowoong.domainwebsocket.config.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.NetworkInterface


@Component
@Profile("prod")
class ServerStartUpEventListener(
    @Value("\${load-balancer-url}")
    private val loadBalancerUrl: String,
    private val restTemplate: RestTemplate
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        // 컨테이너 IP 획득
        val ip = NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .first { it.isSiteLocalAddress }
            .hostAddress

        // 로드밸런서에 서버 Up 상태 전달
        restTemplate.getForObject("$loadBalancerUrl/ws-up?ip=$ip", String::class.java)
    }
}