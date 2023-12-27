import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
}

group = "com.mvp.payment"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

val springVersion by extra { "3.2.1" }

dependencies {

	//Spring
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-tomcat:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-webflux:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-security:$springVersion")
	implementation("org.springframework.session:spring-session-core:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-cache:$springVersion")
	implementation ("io.awspring.cloud:spring-cloud-aws-starter:3.1.0")
	implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:3.1.0")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:$springVersion")

	// Kotlin utils
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")

	//SQS and SNS
	implementation("software.amazon.awssdk:sqs:2.22.5")
	implementation("software.amazon.awssdk:sns:2.22.5")

	// H2
	implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")

	// Coroutines and Reactor not used yet! (used to more imperative reactive programing with suspend functions)
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.1")

	// Mercado Pago SDK
	implementation("com.mercadopago:sdk-java:2.1.14")

	// Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.projectreactor:reactor-test:3.5.4")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("com.h2database:h2:2.2.224")
	testImplementation("io.rest-assured:scala-support:5.4.0")

	//Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.1.0")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// CPF Validator
	//implementation("br.com.colman.simplecpfvalidator:simple-cpf-validator:2.5.1")
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
}
