package test.portfolio.eventsourcing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.springframework.beans.factory.annotation.Autowired
import portfolio.JsonProcessor
import portfolio.eventsourcing.StreamEventEntity
import portfolio.eventsourcing.StreamEventJpaRepository
import java.time.LocalDateTime

@JpaEventSourcingTest
class StreamEventJpaRepository_specs {
    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `findByStreamIdAndScope 메서드는 스트림 식별자와 범위가 같은 이벤트를 조회 한다`(
        userId: String,
        scope: String,
        userCreated: UserCreated,
        userNameChanged: UserNameChanged,
        @Autowired streamEventJpaRepository: StreamEventJpaRepository,
        @Autowired sut: StreamEventJpaRepository,
        @Autowired jsonProcessor: JsonProcessor,
    ) {
        // Arrange
        sut.save(
            StreamEventEntity(
                streamId = userId,
                scope = scope,
                raisedDateTimeUtc = LocalDateTime.now(),
                event = jsonProcessor.toJson(userCreated),
                eventTypeName = UserCreated::class.qualifiedName!!,
                version = 1,
                initiator = null,
            )
        )
        sut.save(
            StreamEventEntity(
                streamId = userId,
                scope = scope,
                raisedDateTimeUtc = LocalDateTime.now(),
                event = jsonProcessor.toJson(userNameChanged),
                eventTypeName = UserNameChanged::class.qualifiedName!!,
                version = 2,
                initiator = null,
            )
        )
        // Act
        val actual = sut.findByStreamIdAndScope(userId, scope)
        // Assert
        assertThat(actual).hasSize(2)
        actual.filter { it.streamId == userId }.forEach {
            when (it.version) {
                1L -> assertThat(jsonProcessor.fromJson(it.event, UserCreated::class.java)).isEqualTo(userCreated)
                2L -> assertThat(jsonProcessor.fromJson(it.event, UserNameChanged::class.java)).isEqualTo(
                    userNameChanged
                )
            }
        }
    }
}
