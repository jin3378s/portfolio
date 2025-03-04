package portfolio.eventsourcing

import org.springframework.data.jpa.repository.JpaRepository

interface StreamEventJpaRepository : JpaRepository<StreamEventEntity, Long> {
    fun findByStreamIdAndScope(streamId: String, scope: String): List<StreamEventEntity>
}

