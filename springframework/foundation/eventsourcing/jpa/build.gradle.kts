import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "1.9.23"
    `java-library`
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("io.freefair.lombok") version "8.6"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
   
    api(project(":foundation:eventsourcing:abstraction"))
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}
