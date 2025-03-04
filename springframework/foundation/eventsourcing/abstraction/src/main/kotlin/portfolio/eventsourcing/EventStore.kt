package portfolio.eventsourcing

import java.time.LocalDateTime

interface EventStore {
    fun collectEvent(
        streamId: String,
        scope: String,
        raisedDateTimeUtc: LocalDateTime,
        event: Any,
        version: Long,
        initiator: String?,
    )

    fun readEvents(streamId: String, scope: String): List<StreamEvent>
}
