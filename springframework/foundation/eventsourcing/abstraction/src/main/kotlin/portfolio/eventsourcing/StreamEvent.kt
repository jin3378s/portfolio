package portfolio.eventsourcing

import java.time.LocalDateTime

data class StreamEvent(
    override val streamId: String,
    override val scope: String,
    override val raisedDateTimeUtc: LocalDateTime,
    override val event: Any,
    override val eventTypeName: String,
    override val version: Long,
    override val initiator: String?,
) : StreamEventModel

interface StreamEventModel : StreamEventProps

interface StreamEventProps {
    val streamId: String
    val scope: String
    val raisedDateTimeUtc: LocalDateTime
    val event: Any
    val eventTypeName: String
    val version: Long
    val initiator: String?
}
