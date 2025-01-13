package potatowoong.springchat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringChatApplication

fun main(args: Array<String>) {
    runApplication<SpringChatApplication>(*args)
}
