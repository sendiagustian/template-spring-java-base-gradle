plugins {
	java
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sendistudio"
version = "0.0.1-SNAPSHOT"
description = "Sene API"

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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    
	// mail
	implementation("org.springframework.boot:spring-boot-starter-mail")
    
	// Resilience4j
	implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
	implementation("org.springframework.boot:spring-boot-starter-aop:3.5.9")

	// Database
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.postgresql:postgresql")
	implementation("com.zaxxer:HikariCP:7.0.2")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security:4.0.2")

	// annotation processor
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")

	// compile only
	compileOnly("org.projectlombok:lombok")

	// dev only
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.platform:junit-platform-launcher")
	testImplementation("org.junit.jupiter:junit-jupiter:5.14.1")
    
    // Scalar API Reference
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:2.8.15")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.41")
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
