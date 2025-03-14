package potatowoong.domainwebsocket.metric.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.DependsOn
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import org.springframework.web.socket.config.WebSocketMessageBrokerStats

@Service
@DependsOn("webSocketMessageBrokerStats")
@SuppressWarnings("removal")
class WebSocketMetricService(
    private val webSocketMessageBrokerStats: WebSocketMessageBrokerStats,
    private val meterRegistry: MeterRegistry
) : ApplicationListener<ContextRefreshedEvent> {

    private val log = KotlinLogging.logger { }

    private fun extractStats(info: String, index: Int): Double {
        val regex = "\\d+".toRegex()
        val stats = regex.findAll(info).map { it.value.toDouble() }.toList()
        return stats.getOrElse(index) { 0.0 }
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val webSocketSessionStats = webSocketMessageBrokerStats.webSocketSessionStats
        val stompSubProtocolStats = webSocketMessageBrokerStats.stompSubProtocolStats
        log.info { "sockjs : ${webSocketMessageBrokerStats.sockJsTaskSchedulerStatsInfo}" }

        // webSocketSession stats
        Gauge.builder("websocket.sessions.total") {
            webSocketSessionStats?.totalSessions?.toDouble() ?: 0.0
        }.description("지금까지 열린 총 웹소켓 세션 수")
            .register(meterRegistry)

        Gauge.builder("websocket.sessions.current") {
            webSocketSessionStats?.webSocketSessions?.toDouble() ?: 0.0
        }.description("현재 열린 웹소켓 세션 수")
            .register(meterRegistry)

        Gauge.builder("websocket.sessions.connect.failure") {
            webSocketSessionStats?.noMessagesReceivedSessions?.toDouble() ?: 0.0
        }.register(meterRegistry)

        Gauge.builder("websocket.sessions.send.limit") {
            webSocketSessionStats?.limitExceededSessions?.toDouble() ?: 0.0
        }.register(meterRegistry)

        Gauge.builder("websocket.sessions.transport.error") {
            webSocketSessionStats?.transportErrorSessions?.toDouble() ?: 0.0
        }.register(meterRegistry)

        // stomp_sub_protocol stats
        Gauge.builder("stomp.total-connect") {
            stompSubProtocolStats?.totalConnect?.toDouble() ?: 0.0
        }.register(meterRegistry)

        Gauge.builder("stomp.total-disconnect") {
            stompSubProtocolStats?.totalDisconnect?.toDouble() ?: 0.0
        }.register(meterRegistry)

        Gauge.builder("stomp.total-connected") {
            stompSubProtocolStats?.totalConnected?.toDouble() ?: 0.0
        }.register(meterRegistry)

        // client_inbound_executor stats
        Gauge.builder("client_inbound_executor_pool_size") {
            extractStats(webSocketMessageBrokerStats.clientInboundExecutorStatsInfo, 0)
        }.register(meterRegistry)

        Gauge.builder("client_inbound_executor_active_thread") {
            extractStats(webSocketMessageBrokerStats.clientInboundExecutorStatsInfo, 1)
        }.register(meterRegistry)

        Gauge.builder("client_inbound_executor_queued_tasks") {
            extractStats(webSocketMessageBrokerStats.clientInboundExecutorStatsInfo, 2)
        }.register(meterRegistry)

        Gauge.builder("client_inbound_executor_completed_tasks") {
            extractStats(webSocketMessageBrokerStats.clientInboundExecutorStatsInfo, 3)
        }.register(meterRegistry)

        // client_outbound_executor stats
        Gauge.builder("client_outbound_executor_pool_size") {
            extractStats(webSocketMessageBrokerStats.clientOutboundExecutorStatsInfo, 0)
        }.register(meterRegistry)

        Gauge.builder("client_outbound_executor_active_thread") {
            extractStats(webSocketMessageBrokerStats.clientOutboundExecutorStatsInfo, 1)
        }.register(meterRegistry)

        Gauge.builder("client_outbound_executor_queued_tasks") {
            extractStats(webSocketMessageBrokerStats.clientOutboundExecutorStatsInfo, 2)
        }.register(meterRegistry)

        Gauge.builder("client_outbound_executor_completed_tasks") {
            extractStats(webSocketMessageBrokerStats.clientOutboundExecutorStatsInfo, 3)
        }.register(meterRegistry)

        // sockjs_task_scheduler stats
        Gauge.builder("sockjs_task_scheduler_pool_size") {
            extractStats(webSocketMessageBrokerStats.sockJsTaskSchedulerStatsInfo, 0)
        }.register(meterRegistry)

        Gauge.builder("sockjs_task_scheduler_active_thread") {
            extractStats(webSocketMessageBrokerStats.sockJsTaskSchedulerStatsInfo, 1)
        }.register(meterRegistry)

        Gauge.builder("sockjs_task_scheduler_queued_tasks") {
            extractStats(webSocketMessageBrokerStats.sockJsTaskSchedulerStatsInfo, 2)
        }.register(meterRegistry)

        Gauge.builder("sockjs_task_scheduler_completed_tasks") {
            extractStats(webSocketMessageBrokerStats.sockJsTaskSchedulerStatsInfo, 3)
        }.register(meterRegistry)
    }
}