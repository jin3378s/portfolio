package portfolio.eventsourcing

import org.springframework.stereotype.Component
import portfolio.ErrorDetail
import portfolio.InvariantViolationException
import portfolio.JsonProcessor
import java.time.LocalDateTime

@Component
class JpaEventStore(
    private val streamEventJpaRepository: StreamEventJpaRepository,
    private val jsonProcessor: JsonProcessor,
) : EventStore {
    override fun collectEvent(
        streamId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: Any,
        version: Long,
        initiator: String?,
    ) {
        try {
            streamEventJpaRepository.save(
                StreamEventEntity(
                    streamId = streamId,
                    scope = scope,
                    raisedDateTimeUtc = raisedDateTimeUtc,
                    event = jsonProcessor.toJson(event),
                    eventTypeName = event.javaClass.packageName + "." + event.javaClass.simpleName,
                    version = version,
                    initiator = initiator,
                )
            )
        } catch (e: Exception) {
            throw InvariantViolationException(ErrorDetail("FailedToCollectEvent", e.message ?: ""))
        }
    }

    override fun readEvents(streamId: String, scope: String): List<StreamEvent> {
        return streamEventJpaRepository
            .findByStreamIdAndScope(streamId, scope)
            .map {
                StreamEvent(
                    streamId = it.streamId,
                    scope = it.scope,
                    raisedDateTimeUtc = it.raisedDateTimeUtc,
                    event = jsonProcessor.fromJson(it.event, Class.forName(it.eventTypeName)),
                    eventTypeName = it.eventTypeName,
                    version = it.version,
                    initiator = it.initiator,
                )
            }
    }
}
