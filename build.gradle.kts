plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sendistudio"
version = "0.0.1-SNAPSHOT"
description = "Base project for Spring Boot with gradle"

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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
	testImplementation("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
        // Tampilkan output standar (sout/println)
        showStandardStreams = true
        
        // Tampilkan event apa saja yang mau di-log
        events("passed", "skipped", "failed", "standard_out", "standard_error")
    }
}
