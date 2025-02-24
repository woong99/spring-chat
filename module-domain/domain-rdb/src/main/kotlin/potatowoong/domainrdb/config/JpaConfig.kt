package potatowoong.domainrdb.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = ["potatowoong.domainrdb.domains.*.entity"])
@EnableJpaRepositories(basePackages = ["potatowoong.domainrdb.domains.*.repository"])
class JpaConfig(
) {

    @Bean
    fun auditorAware(): AuditorAware<Long> {
        return AuditorAwareImpl()
    }
}