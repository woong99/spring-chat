package potatowoong.springchat.global.config.db

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
class JpaConfig(
) {

    @Bean
    fun auditorAware(): AuditorAware<Long> {
        return AuditorAwareImpl()
    }
}