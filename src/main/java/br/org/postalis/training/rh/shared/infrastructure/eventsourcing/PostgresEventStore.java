// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/PostgresEventStore.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import br.org.postalis.training.rh.shared.domain.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class PostgresEventStore implements EventStore {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final EventTypeRegistry eventTypeRegistry;


    @Override
    @Transactional
    public void save(String aggregateId, String aggregateType,
                     List<DomainEvent> events, long expectedVersion) {

        // 1. Verificar versão atual (Optimistic Locking)
        Long currentVersion = getCurrentVersion(aggregateId).orElse(0L);

        if (!currentVersion.equals(expectedVersion)) {
            throw new ConcurrencyException(
                    "Conflito de concorrência: esperado versão " + expectedVersion +
                            ", encontrado " + currentVersion
            );
        }

        // 2. Persistir cada evento
        long sequenceNumber = currentVersion;
        for (DomainEvent event : events) {
            sequenceNumber++;
            persistEvent(aggregateId, aggregateType, event, sequenceNumber);
        }
    }

    private void persistEvent(String aggregateId, String aggregateType,
                              DomainEvent event, long sequenceNumber) {
        String sql = """
                INSERT INTO rh.domain_events
                (event_id, aggregate_id, aggregate_type, event_type, event_version,
                 sequence_number, payload, metadata, occurred_on)
                VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?)
                """;

        try {
            jdbcTemplate.update(sql,
                    event.eventId().toString(),
                    aggregateId,
                    aggregateType,
                    event.eventType(),
                    event.version(),
                    sequenceNumber,
                    objectMapper.writeValueAsString(event),
                    null, // metadata
                    Timestamp.from(event.occurredOn())
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao persistir evento: " + event.eventType(), e);
        }
    }

    @Override
    public List<DomainEvent> load(String aggregateId) {
        String sql = """
                SELECT event_type, payload
                FROM rh.domain_events
                WHERE aggregate_id = ?::uuid
                ORDER BY sequence_number ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String eventType = rs.getString("event_type");
            String payload = rs.getString("payload");
            return deserializeEvent(eventType, payload);
        }, aggregateId);
    }

    @Override
    public List<DomainEvent> loadFromVersion(String aggregateId, long fromVersion) {
        String sql = """
                SELECT event_type, payload
                FROM rh.domain_events
                WHERE aggregate_id = ?::uuid
                  AND sequence_number > ?
                ORDER BY sequence_number ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String eventType = rs.getString("event_type");
            String payload = rs.getString("payload");
            return deserializeEvent(eventType, payload);
        }, aggregateId, fromVersion);
    }

    private Optional<Long> getCurrentVersion(String aggregateId) {
        String sql = """
                SELECT MAX(sequence_number)
                FROM rh.domain_events
                WHERE aggregate_id = ?::uuid
                """;

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql, Long.class, aggregateId)
        );
    }

    private DomainEvent deserializeEvent(String eventType, String payload) {
        // NOTA: Versão simplificada - não considera event_version
        // Para múltiplas versões, veja Módulo 08 - Event Versioning
        try {
            Class<? extends DomainEvent> eventClass =
                    eventTypeRegistry.getEventClass(eventType);
            return objectMapper.readValue(payload, eventClass);
        } catch (Exception e) {
            throw new RuntimeException("Erro deserializando evento: " + eventType, e);
        }
    }

    @Override
    public long getVersion(String aggregateId) {
        return getCurrentVersion(aggregateId).orElse(0L);
    }

    @Override
    public List<DomainEvent> loadByAggregateType(String aggregateType) {
        String sql = """
        SELECT aggregate_id, event_type, payload, sequence_number, occurred_on
        FROM rh.domain_events
        WHERE aggregate_type = ?
        ORDER BY occurred_on, sequence_number
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String eventType = rs.getString("event_type");
            String payload = rs.getString("payload");
            return deserializeEvent(eventType, payload);
        }, aggregateType);
    }
}