import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    // Kotlin
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    // Spring Boot
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"

    // Coverage & Docs
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

apply(from = "gradle/coloredTesting.gradle")
apply(plugin = "org.jetbrains.kotlinx.kover")

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = "2023.0.3"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Database & Migrations
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Micrometer
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Dev Tools
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // Spring Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveBaseName.set("demo-app")
    manifest {
        attributes["Main-Class"] = "org.springframework.boot.loader.JarLauncher"
    }
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.build {
    dependsOn(tasks.jar)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

tasks.named("koverVerify").configure {
    doLast {
        val xmlFile = file("build/reports/kover/xml/report.xml")
        if (!xmlFile.exists()) {
            println("Coverage report not found. Ensure tests were executed and Kover is properly configured.")
            return@doLast
        }

        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(xmlFile)

        val counters = doc.getElementsByTagName("counter")
        var totalLines = 0
        var coveredLines = 0

        for (i in 0 until counters.length) {
            val counterNode = counters.item(i) as? Element ?: continue
            if (counterNode.getAttribute("type") == "LINE") {
                val missed = counterNode.getAttribute("missed").toIntOrNull() ?: 0
                val covered = counterNode.getAttribute("covered").toIntOrNull() ?: 0
                totalLines += missed + covered
                coveredLines += covered
            }
        }

        if (totalLines > 0) {
            val lineCoverage = (coveredLines.toDouble() / totalLines) * 100
            println("Test Coverage Results")
            println("Line coverage: %.2f%%".format(lineCoverage))
        }
    }
}

kover {
    xmlReport {
        onCheck = true
    }

    verify {
        rule {
            isEnabled = true
            name = "Coverage must be more than 75%"
            bound {
                minValue = 75
            }
        }
    }
}
