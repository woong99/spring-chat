package potatowoong.domainrdb.config

import org.springframework.data.domain.AuditorAware
import java.util.*

class AuditorAwareImpl : AuditorAware<Long> {

    override fun getCurrentAuditor(): Optional<Long> {
//        val authentication = SecurityContextHolder.getContext().authentication

//        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
//            return Optional.of(0L)
//        }

//        return Optional.of(authentication.name.toLong())
        return Optional.of(0L)
    }
}