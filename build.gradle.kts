plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.allopen") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

apply(plugin = "kotlin-jpa")

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "potatowoong"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

configurations {
    configureEach {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.1.0:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Log4j2
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // Kotlin-Logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

    // WebSocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // P6Spy
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // MariaDB
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("pl.tkowalcz.tjahzi:log4j2-appender:0.9.32")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// 환경별 설정 파일을 사용하기 위한 설정
val profile: String by project
val activeProfile = if (!project.hasProperty("profile") || profile.isEmpty()) "local" else profile
project.ext.set("profile", activeProfile)

sourceSets {
    main {
        resources {
            setSrcDirs(listOf("src/main/resources", "src/main/resources-$activeProfile"))
        }
    }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        filesMatching("**/application.yaml") {
            expand(project.properties)
        }
    }
}
