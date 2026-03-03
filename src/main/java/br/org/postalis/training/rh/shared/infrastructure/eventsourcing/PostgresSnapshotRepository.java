// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/PostgresSnapshotRepository.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PostgresSnapshotRepository implements SnapshotRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresSnapshotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void save(AggregateSnapshot snapshot) {
        String sql = """
                INSERT INTO rh.aggregate_snapshots
                (snapshot_id, aggregate_id, aggregate_type, version, state)
                VALUES (?::uuid, ?::uuid, ?, ?, ?::jsonb)
                ON CONFLICT (aggregate_id, version) DO NOTHING
                """;

        jdbcTemplate.update(sql,
                snapshot.snapshotId(),
                snapshot.aggregateId(),
                snapshot.aggregateType(),
                snapshot.version(),
                snapshot.state()
        );
    }

    @Override
    public Optional<AggregateSnapshot> findLatest(String aggregateId) {
        String sql = """
                SELECT snapshot_id, aggregate_id, aggregate_type, version, state, created_at
                FROM rh.aggregate_snapshots
                WHERE aggregate_id = ?::uuid
                ORDER BY version DESC
                LIMIT 1
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new AggregateSnapshot(
                                UUID.fromString(rs.getString("snapshot_id")),
                                rs.getString("aggregate_id"),
                                rs.getString("aggregate_type"),
                                rs.getLong("version"),
                                rs.getString("state"),
                                rs.getTimestamp("created_at").toInstant()
                        ),
                aggregateId
        ).stream().findFirst();
    }
}