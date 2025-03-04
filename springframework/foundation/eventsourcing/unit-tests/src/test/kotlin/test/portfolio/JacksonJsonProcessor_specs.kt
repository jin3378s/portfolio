package test.portfolio

import autoparams.kotlin.AutoKotlinSource
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import portfolio.JacksonJsonProcessor

class JacksonJsonProcessor_specs {
    @ParameterizedTest
    @AutoKotlinSource
    fun `fromJson 메서드는 객체를 올바르게 역직렬화 한다`(
        testObject: TestObject
    ) {
        // Arrange
        val sut = JacksonJsonProcessor(ObjectMapper().registerKotlinModule())

        // Act
        val actual = sut.fromJson("""
            {
                "name": "${testObject.name}",
                "age": ${testObject.age}
            }
        """.trimIndent(), TestObject::class.java)
        // Assert

        assertThat(actual).isEqualTo(testObject)
    }
    data class TestObject(val name: String, val age: Int)

    @ParameterizedTest
    @AutoKotlinSource
    fun `toJson 메서드는 객체를 올바르게 직렬화 한다`(
        expected: TestObject
    ) {
        // Arrange
        val sut = JacksonJsonProcessor(ObjectMapper().registerKotlinModule())

        // Act
        val actual = sut.toJson(expected)
        // Assert

        assertThat(sut.fromJson(actual, TestObject::class.java)).isEqualTo(expected)
    }
}
