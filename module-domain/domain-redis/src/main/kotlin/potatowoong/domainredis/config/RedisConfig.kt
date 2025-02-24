package potatowoong.domainredis.config

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {

    /**
     * Redis 연결을 위한 'Connection' 생성
     */
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(redisProperties.host, redisProperties.port)
    }

    /**
     * Redis 데이터 처리를 위한 템플릿 생성
     */
    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()

        // Redis 연결
        redisTemplate.connectionFactory = redisConnectionFactory()

        // Key-Value 형태로 직렬화
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()

        // Hash Key-Value 형태로 직렬화
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = StringRedisSerializer()

        // Default Serializer
        redisTemplate.setDefaultSerializer(StringRedisSerializer())

        return redisTemplate
    }
}