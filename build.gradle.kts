import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jacksonVersion = "2.9.8"

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
	kotlin("plugin.serialization") version "1.9.21"
	kotlin("plugin.jpa") version "1.9.21"
	jacoco
}

group = "com.mvp.payment"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

configurations.all {
	exclude(group = "commons-logging", module = "commons-logging")
}

val ktorVersion by extra { "2.3.7" }

dependencies {

	//Spring
	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.mongodb:mongodb-driver-sync:4.11.1")

	//AWS
	implementation ("io.awspring.cloud:spring-cloud-aws-starter:3.1.0")
	implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:3.1.0")

	//Ktor
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-apache:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
	implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

	// Kotlin utils
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

	//SQS and SNS
	implementation("software.amazon.awssdk:sqs:2.22.5")
	implementation("software.amazon.awssdk:sns:2.22.5")

	// Mercado Pago SDK
	implementation("com.mercadopago:sdk-java:2.1.14")

	// Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("com.h2database:h2:2.2.224")
	testImplementation("io.rest-assured:rest-assured:5.4.0")
	implementation("io.rest-assured:json-schema-validator:5.4.0")
	testImplementation("io.rest-assured:json-path:5.4.0")
	testImplementation("io.rest-assured:xml-path:5.4.0")
	testImplementation("io.rest-assured:spring-mock-mvc:5.4.0")
	testImplementation("io.rest-assured:kotlin-extensions:5.4.0")

	//Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.1.0")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// Cucumber
	implementation("io.cucumber:cucumber-spring:7.15.0")
	implementation("io.cucumber:cucumber-java:7.15.0")
	implementation("io.cucumber:cucumber-junit:7.15.0")

	testImplementation("org.jacoco:org.jacoco.core:0.8.11")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	enabled = true
	useJUnitPlatform()
	systemProperty("cucumber.junit-platform.naming-strategy", "long")
	finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("cucumber") {
	useJUnitPlatform {
		includeEngines("cucumber")
	}
	reports {
		html.required.set(true)
		junitXml.required.set(true)
	}
	testClassesDirs = sourceSets["test"].output.classesDirs
	classpath = sourceSets["test"].runtimeClasspath
	finalizedBy(tasks.jacocoTestReport)
}

jacoco {
	toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	val excludes = listOf("**/configuration/*", "**/model/*",
			"**/utils/*", "**/com/mvp/order/OrderApplication.kt", "**/com/mvp/order/infrastruture/entity/*")
	classDirectories.setFrom(files(classDirectories.files.map {
		fileTree(it).apply {
			exclude(excludes)
		}
	}))
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		val excludes = listOf("**/configuration/**", "**/com/mvp/order/domain/model/**",
				"**/utils/**", "**/com/mvp/order/OrderApplication.kt", "**/com/mvp/order/infrastruture/entity/**")
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it).exclude(excludes)
		}))
		rule {
			limit {
				minimum = BigDecimal.valueOf(0.8)  // 80% coverage
			}
		}
	}
}
