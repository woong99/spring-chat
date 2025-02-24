package potatowoong.domainwebsocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import potatowoong.domainmongo.config.DomainMongoConfig
import potatowoong.domainrdb.config.DomainRdbConfig
import potatowoong.domainredis.config.DomainRedisConfig
import potatowoong.modulesecurity.config.ModuleSecurityConfig

@SpringBootApplication
@Import(value = [DomainRdbConfig::class, DomainMongoConfig::class, DomainRedisConfig::class, ModuleSecurityConfig::class])
class DomainWebsocketApplication

fun main(args: Array<String>) {
    runApplication<DomainWebsocketApplication>(*args)
}
