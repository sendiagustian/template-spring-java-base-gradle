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
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
    
    // Scalar API Reference
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.2")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.46")
    
    // JSON-P APIs
    implementation("jakarta.json:jakarta.json-api:2.1.3")
    runtimeOnly("org.eclipse.parsson:jakarta.json:1.1.5")
    
    // JDBC
	implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // Database drivers — bundles every `engine:` found in properties/database.yaml,
    // so changing the engine there is the only step needed. Override for slim builds
    // (e.g. Docker) with -PdbEngines=postgresql (comma-separated).
    // Add a new engine: one entry here + one constant in app/properties/DatabaseEngine.java
    val supportedDrivers = mapOf(
        "sqlite"     to "org.xerial:sqlite-jdbc:3.47.1.0",
        "postgresql" to "org.postgresql:postgresql",        // version from Spring BOM
        "mysql"      to "com.mysql:mysql-connector-j",      // version from Spring BOM
        "oracle"     to "com.oracle.database.jdbc:ojdbc11", // version from Spring BOM
    )

    // Matches `engine: sqlite` and `engine: ${DB_ENGINE:postgresql}` (placeholder default)
    val engineRegex = Regex("""engine:\s*(?:\$\{[^:}]+:)?([A-Za-z]+)\}?""")
    val databaseYaml = file("src/main/resources/properties/database.yaml")

    val dbEngines = (findProperty("dbEngines") as String?)
        ?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotEmpty() }?.toSet()
        ?: databaseYaml.takeIf { it.exists() }?.let { yaml ->
            engineRegex.findAll(yaml.readText()).map { it.groupValues[1].lowercase() }.toSet()
        }?.takeIf { it.isNotEmpty() }
        ?: setOf("sqlite").also {
            logger.warn("No engines found in database.yaml — defaulting to sqlite driver only")
        }

    dbEngines.forEach { engine ->
        val artifact = supportedDrivers[engine]
            ?: error("Unknown dbEngine '$engine'. Supported: ${supportedDrivers.keys}")
        runtimeOnly(artifact)
    }
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
