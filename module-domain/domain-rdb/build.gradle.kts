import org.springframework.boot.gradle.tasks.bundling.BootJar

val bootJar: BootJar by tasks
val jar: Jar by tasks

bootJar.enabled = false
jar.enabled = true

plugins {
    kotlin("plugin.jpa") version "1.9.25" apply false
    kotlin("plugin.allopen") version "1.9.25" apply false
    kotlin("kapt") version "1.9.25" apply false
}

apply(plugin = "kotlin-jpa")

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":module-common"))
    implementation(project(":module-security"))

    // Spring Data JPA
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    // P6Spy
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")

    // MariaDB
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // QueryDSL
    implementation("io.github.openfeign.querydsl:querydsl-jpa:6.10.1")
    implementation("io.github.openfeign.querydsl:querydsl-apt:6.10.1")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    kapt("io.github.openfeign.querydsl:querydsl-apt:6.10.1:jpa")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}
