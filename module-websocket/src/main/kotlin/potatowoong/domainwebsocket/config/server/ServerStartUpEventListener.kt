package potatowoong.domainwebsocket.config.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import potatowoong.domainwebsocket.common.utils.IpUtils

@Component
@Profile("dev")
class ServerStartUpEventListener(
    @Value("\${load-balancer-url}") private val loadBalancerUrl: String,
    @Value("\${deploy}") private val deploy: String,
    private val restTemplate: RestTemplate,
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        // 컨테이너 IP 획득
        val ip = IpUtils.getLocalIp()

        // 로드밸런서에 서버 Up 상태 전달
        val uri = UriComponentsBuilder.fromUriString("$loadBalancerUrl/ws-up")
            .queryParam("ip", ip)
            .queryParam("deploy", deploy)
            .build()
            .toUriString()
        restTemplate.getForObject(uri, String::class.java)
    }
}
