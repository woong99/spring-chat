package potatowoong.moduleeureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class ModuleEurekaApplication

fun main(args: Array<String>) {
    runApplication<ModuleEurekaApplication>(*args)
}
