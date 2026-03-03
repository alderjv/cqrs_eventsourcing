package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.EventStore;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.SnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresFuncionarioRepository extends FuncionarioRepository {

    private final EventDispatcher dispatcher;
    private final JdbcTemplate jdbc;

    public PostgresFuncionarioRepository(EventStore eventStore, SnapshotRepository snapshotRepository, FuncionarioProjector projector, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, EventDispatcher dispatcher, JdbcTemplate jdbc) {
        super(eventStore, snapshotRepository, projector, jdbcTemplate, objectMapper);
        this.dispatcher = dispatcher;
        this.jdbc = jdbc;
    }

    public Optional<Funcionario> findById(UUID id) {
        var rows = jdbc.queryForList("""
                SELECT event_type, event_version, event_data 
                FROM rh.domain_events 
                WHERE aggregate_id = ? 
                ORDER BY version
                """, id);

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        var events = rows.stream()
                .map(row -> dispatcher.deserialize(
                        (String) row.get("event_type"),
                        (Integer) row.get("event_version"),
                        (JsonNode) row.get("event_data")
                ))
                .toList();

        return Optional.of(Funcionario.fromEvents(events, () -> new Funcionario(id)));
    }

}
