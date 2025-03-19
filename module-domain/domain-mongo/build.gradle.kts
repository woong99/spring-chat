import org.springframework.boot.gradle.tasks.bundling.BootJar

val bootJar: BootJar by tasks
val jar: Jar by tasks

bootJar.enabled = false
jar.enabled = true

//plugins {
//    kotlin("plugin.jpa") version "1.9.25" apply false
//    kotlin("plugin.allopen") version "1.9.25" apply false
//}
//
//apply(plugin = "kotlin-jpa")
//
//allOpen {
//    annotation("jakarta.persistence.Entity")
//    annotation("jakarta.persistence.MappedSuperclass")
//    annotation("jakarta.persistence.Embeddable")
//}

dependencies {
    implementation(project(":module-security"))

    // MongoDB
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
}
