package portfolio.eventsourcing

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "stream_events",
    indexes = [
        Index(name = "stream_events_stream_id_idx", columnList = "streamId, scope, version", unique = true),
    ]
)
class StreamEventEntity(
    @Column(nullable = false)
    override val streamId: String,
    @Column(nullable = false)
    override val scope: String,
    @Column(nullable = false)
    override val raisedDateTimeUtc: LocalDateTime,
    @Column(nullable = false)
    override val event: String,
    @Column(nullable = false)
    override val eventTypeName: String,
    @Column(nullable = false)
    override val version: Long,
    override val initiator: String?,
) : StreamEventModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val sequence: Long = 0L
}
