package potatowoong.modulesse.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsUtils
import potatowoong.modulesecurity.auth.jwt.components.JwtTokenProvider
import potatowoong.modulesecurity.auth.jwt.filter.JwtAuthenticationFilter
import potatowoong.modulesecurity.auth.jwt.handler.JwtAccessDeniedHandler
import potatowoong.modulesecurity.auth.jwt.handler.JwtAuthenticationEntryPoint

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) {

    /**
     * PasswordEncoder Bean 등록
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /**
     * Security 설정
     */
    @Bean
    fun filterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        // Basic Auth 비활성화
        http
            .httpBasic { it.disable() }

        // CSRF 비활성화
        http
            .csrf { it.disable() }

        // Form Login 비활성화
        http
            .formLogin { it.disable() }

        // Session 비활성화
        http
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        // URL 권한 설정
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers(*PERMIT_ALL).permitAll()
                    .anyRequest().authenticated()
            }

        // 예외 처리 설정
        http.exceptionHandling {
            it
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        }

        // JwtAuthenticationFilter 설정
        http
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }

    companion object {
        // 허용 URL
        private val PERMIT_ALL = arrayOf(
            "/ws-stomp/**",
        )
    }
}
