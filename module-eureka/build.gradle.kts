import org.springframework.boot.gradle.tasks.bundling.BootJar

val bootJar: BootJar by tasks
val jar: Jar by tasks

bootJar.enabled = true
jar.enabled = false

configurations {
    configureEach {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

dependencies {
    // Eureka Server
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:4.2.0")
}