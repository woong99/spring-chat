package potatowoong.domainwebsocket.common.utils

import java.net.NetworkInterface

object IpUtils {

    /**
     * 내부 IP 주소 반환
     */
    fun getLocalIp(): String = NetworkInterface.getNetworkInterfaces().toList()
        .flatMap { it.inetAddresses.toList() }
        .first { it.isSiteLocalAddress }
        .hostAddress
}