package potatowoong.domainredis.utils

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisUtils(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    /**
     * Set data to Redis Sets
     */
    fun setDataToSet(key: String, value: Any) {
        redisTemplate.opsForSet().add(key, value)
    }

    /**
     * Get date to Redis Sets
     */
    fun getDataFromSet(key: String): MutableSet<Any>? {
        return redisTemplate.opsForSet().members(key)
    }

    /**
     * Delete data from Redis Sets
     */
    fun deleteDataFromSet(key: String, value: Any) {
        redisTemplate.opsForSet().remove(key, value)
    }
}