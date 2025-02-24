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
    implementation(project(":module-domain:domain-mongo"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
}