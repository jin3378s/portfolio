package test.portfolio.eventsourcing

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.params.ParameterizedTest
import org.springframework.beans.factory.annotation.Autowired
import portfolio.InvariantViolationException
import portfolio.eventsourcing.JpaEventStore
import portfolio.eventsourcing.StreamEventJpaRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@JpaEventSourcingTest
class JpaEventStore_specs {
    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `collectEvent 메서드는 이벤트를 수집 한다`(
        userId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: UserCreated,
        @Autowired streamEventJpaRepository: StreamEventJpaRepository,
        @Autowired sut: JpaEventStore,
    ) {
        // Arrange
        // Act
        sut.collectEvent(
            streamId = userId,
            scope = scope,
            raisedDateTimeUtc = raisedDateTimeUtc,
            event = event,
            version = 1,
            null
        )
        // Assert
        streamEventJpaRepository
            .findAll()
            .last { it.streamId == userId }
            .let {
                assertThat(it.streamId).isEqualTo(userId)
                assertThat(it.scope).isEqualTo(scope)
                assertThat(it.raisedDateTimeUtc).isCloseTo(raisedDateTimeUtc, within(1, ChronoUnit.SECONDS))
                assertThat(it.streamId).isEqualTo(userId)
            }
    }

    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `collectEvent 메서드는 이벤트를 수집 할 때 이벤트타입을 올바르게 저장 한다`(
        userId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: UserCreated,
        @Autowired streamEventJpaRepository: StreamEventJpaRepository,
        @Autowired sut: JpaEventStore,
    ) {
        // Arrange
        // Act
        sut.collectEvent(
            streamId = userId,
            scope = scope,
            raisedDateTimeUtc = raisedDateTimeUtc,
            event = event,
            version = 1,
            null
        )
        // Assert
        streamEventJpaRepository
            .findAll()
            .last { it.streamId == userId }
            .let {
                assertThat(it.eventTypeName).isEqualTo(UserCreated::class.qualifiedName)
            }
    }

    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `readEvents 메서드는 스트림 식별자와 범위가 같은 이벤트를 조회 한다`(
        userId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: UserCreated,
        @Autowired sut: JpaEventStore,
    ) {
        // Arrange
        sut.collectEvent(
            streamId = userId,
            scope = scope,
            raisedDateTimeUtc = raisedDateTimeUtc,
            event = event,
            version = 1,
            null
        )
        // Act && Assert
        sut.readEvents(streamId = userId, scope = scope).let {
            assertThat(it).hasSize(1)
        }
    }

    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `readEvents 메서드는 이벤트 타입으로 역직렬화 된 이벤트 리스트를 반환 한다`(
        userId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: UserCreated,
        @Autowired sut: JpaEventStore,
    ) {
        // Arrange
        sut.collectEvent(
            streamId = userId,
            scope = scope,
            raisedDateTimeUtc = raisedDateTimeUtc,
            event = event,
            version = 1,
            null
        )
        // Act && Assert
        sut.readEvents(streamId = userId, scope = scope).last().let {
            assertThat(it.event).isInstanceOf(UserCreated::class.java)
        }
    }

    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `collectEvent 메서드는 같은 스트림 식별자와 스코프를 가진 이벤트의 동일 버전을 수집하면 예외를 반환 한다`(
        userId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: UserCreated,
        @Autowired sut: JpaEventStore,
    ) {
        // Arrange
        sut.collectEvent(
            streamId = userId,
            scope = scope,
            raisedDateTimeUtc = raisedDateTimeUtc,
            event = event,
            version = 1,
            null
        )
        // Act && Assert
        assertThatThrownBy {
            sut.collectEvent(
                streamId = userId,
                scope = scope,
                raisedDateTimeUtc = raisedDateTimeUtc,
                event = event,
                version = 1,
                null
            )
        }.isInstanceOf(InvariantViolationException::class.java)
    }

    @ParameterizedTest
    @AutoJpaEventSourcingSource
    fun `collectEvent 메서드는 같은 스트림 식별자의 다른 스코프를 가진 이벤트의 동일 버전을 수집 할 수 있다`(
        streamId: String,
        scope: String,
        anotherScope: String,
        userCreated: UserCreated,
        @Autowired sut: JpaEventStore,
    ) {
        // Arrange
        sut.collectEvent(
            streamId = streamId,
            scope = scope,
            raisedDateTimeUtc = LocalDateTime.now(),
            event = userCreated,
            version = 1,
            null
        )
        // Act
        assertThatCode {
            sut.collectEvent(
                streamId = streamId,
                scope = anotherScope,
                raisedDateTimeUtc = LocalDateTime.now(),
                event = userCreated,
                version = 1,
                null
            )
        }.doesNotThrowAnyException()
    }
}
