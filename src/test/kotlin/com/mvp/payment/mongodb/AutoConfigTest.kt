package com.mvp.payment.mongodb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension


@DataMongoTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@EnableAutoConfiguration
class AutoConfigTest {

    @Test
    fun example(@Autowired mongoTemplate: MongoTemplate) {
        assertThat(mongoTemplate.db).isNotNull()
        val names = mongoTemplate.db
            .listCollectionNames()
            .into(ArrayList())

        assertThat(names).isEmpty()
    }
}