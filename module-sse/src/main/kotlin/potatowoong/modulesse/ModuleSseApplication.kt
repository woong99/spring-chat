package potatowoong.modulesse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import potatowoong.domainmongo.config.DomainMongoConfig
import potatowoong.domainrdb.config.DomainRdbConfig
import potatowoong.domainredis.config.DomainRedisConfig
import potatowoong.modulesecurity.config.ModuleSecurityConfig

@SpringBootApplication
@Import(value = [DomainMongoConfig::class, DomainRedisConfig::class, DomainRdbConfig::class, ModuleSecurityConfig::class])
class ModuleSseApplication

fun main(args: Array<String>) {
    runApplication<ModuleSseApplication>(*args)
}
