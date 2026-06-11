plugins {
	java
	id("org.springframework.boot") version "4.0.7"
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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    
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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.2")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.46")
    
    // JSON-P APIs
    implementation("jakarta.json:jakarta.json-api:2.1.3")
    runtimeOnly("org.eclipse.parsson:jakarta.json:1.1.5")
    
    // JDBC
	implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // Database drivers — SQLite is default (local). Add others as needed.
    runtimeOnly("org.xerial:sqlite-jdbc:3.47.1.0")
    // runtimeOnly("org.postgresql:postgresql")
    // runtimeOnly("com.mysql:mysql-connector-j")
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
