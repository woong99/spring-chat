package potatowoong.modulesse.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import potatowoong.modulesecurity.auth.jwt.components.JwtTokenProvider
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun filterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain {
        // Basic Auth 비활성화
        http
            .httpBasic { it.disable() }

        // CSRF 비활성화
        http
            .csrf { it.disable() }

        // Form Login 비활성화
        http
            .formLogin { it.disable() }

        // Stateless Session 설정
        http
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

        // URL 권한 설정
        http
            .authorizeExchange {
                it.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                it.anyExchange().authenticated()
            }

        http
            .exceptionHandling {
                // TODO : 예외 처리 추가
            }

        // JwtAuthenticationFilter 설정
        http
            .addFilterBefore(
                authenticationWebFilter(),
                SecurityWebFiltersOrder.AUTHENTICATION
            )

        return http.build()
    }

    /**
     * AuthenticationWebFilter 설정
     */
    private fun authenticationWebFilter(): AuthenticationWebFilter {
        val authenticationManager = ReactiveAuthenticationManager { data: Authentication? ->
            Mono.just(
                data!!
            )
        }

        val authenticationWebFilter = AuthenticationWebFilter(authenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter())
        return authenticationWebFilter
    }

    /**
     * JWT Token 인증 처리
     */
    private fun serverAuthenticationConverter(): ServerAuthenticationConverter {
        return ServerAuthenticationConverter { exchange ->
            val token = exchange.request.headers.getFirst("Authorization")
            if (token != null && token.startsWith("Bearer ")) {
                val authentication = jwtTokenProvider.getAuthentication(token.substring(7))
                SecurityContextHolder.getContext().authentication = authentication
                
                return@ServerAuthenticationConverter Mono.just(authentication)
            }
            Mono.empty()
        }
    }
}
