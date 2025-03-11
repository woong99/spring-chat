import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
    kotlin("plugin.allopen") version "1.9.25" apply false
    kotlin("kapt") version "1.9.25" apply false
    id("org.springframework.boot") version "3.4.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "potatowoong"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
        }
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")

    dependencies {
        // Kotlin
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // Log4j2
        implementation("org.springframework.boot:spring-boot-starter-log4j2")

        // Kotlin-Logging
        implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

        // Jasypt
        implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    }

    tasks.register("prepareKotlinBuildScriptModel") {}
}
