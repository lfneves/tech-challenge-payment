package com.mvp.payment.domain.configuration

import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class MongoConfig: EmbeddedMongoAutoConfiguration()