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
    implementation(project(":module-common"))
    implementation(project(":module-security"))
    implementation(project(":module-domain:domain-rdb"))
    implementation(project(":module-domain:domain-mongo"))
    implementation(project(":module-domain:domain-redis"))

    // WebSocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Eureka Client
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.2.0")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // P6Spy
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")
}