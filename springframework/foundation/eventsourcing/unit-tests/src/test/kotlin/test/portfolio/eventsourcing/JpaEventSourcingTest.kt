package test.portfolio.eventsourcing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import portfolio.JacksonJsonProcessor
import portfolio.JsonProcessor
import portfolio.eventsourcing.JpaEventStore
import portfolio.eventsourcing.StreamEventJpaRepository

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@SpringBootTest(
    classes = [JpaEventSourcingTestConfiguration::class],
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    ]
)
annotation class JpaEventSourcingTest

@SpringBootConfiguration
@EnableJpaRepositories(basePackages = ["portfolio.eventsourcing"])
@EnableAutoConfiguration
@EntityScan(basePackages = ["portfolio.eventsourcing"])
class JpaEventSourcingTestConfiguration {
    @Bean
    fun jsonProcessor(): JsonProcessor {
        return JacksonJsonProcessor(ObjectMapper().registerKotlinModule())
    }

    @Bean
    fun jpaEventStore(streamEventJpaRepository: StreamEventJpaRepository): JpaEventStore {
        return JpaEventStore(streamEventJpaRepository, jsonProcessor())
    }
}
