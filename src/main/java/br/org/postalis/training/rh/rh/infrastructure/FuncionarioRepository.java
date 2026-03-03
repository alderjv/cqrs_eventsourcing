// src/main/java/br/org/postalis/training/rh/funcionario/infrastructure/FuncionarioRepository.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.rh.domain.FuncionarioSnapshot;
import br.org.postalis.training.rh.shared.domain.AggregateRepository;
import br.org.postalis.training.rh.shared.domain.AggregateRoot;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import br.org.postalis.training.rh.shared.domain.UUIDv7;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.AggregateSnapshot;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.EventStore;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.SnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
@Log4j2
public class FuncionarioRepository implements AggregateRepository<Funcionario> {

    private final EventStore eventStore;
    private final SnapshotRepository snapshotRepository;
    private final FuncionarioProjector projector;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private static final int SNAPSHOT_INTERVAL = 100;

    @Override
    @Transactional
    public void save(Funcionario funcionario) {
        List<DomainEvent> events = funcionario.getUncommittedEvents();

        if (events.isEmpty()) {
            return;
        }

        // 1. Buscar versão esperada do Event Store
        long expectedVersion = eventStore.getVersion(funcionario.getId().toString());

        // 2. Persistir eventos no Event Store
        eventStore.save(
                funcionario.getId().toString(),
                "Funcionario",
                events,
                expectedVersion
        );

        // 3. Projetar para tabela 3NF (MESMA TRANSAÇÃO!)
        for (DomainEvent event : events) {
            projector.project(event);
        }

        // 4. Limpar eventos não commitados
        funcionario.markEventsAsCommitted();

        // 5. Verifica se precisa criar snapshot
        if (shouldCreateSnapshot(funcionario.getVersion())) {
            createSnapshot(funcionario);
        }
    }
/*
    @Override
    public Optional<Funcionario> findById(UUID id) {
        List<DomainEvent> events = eventStore.load(id.toString());

        if (events.isEmpty()) {
            return Optional.empty();
        }

        // Reconstruir Aggregate dos eventos
        // NOTA: Extraímos o aggregateId do primeiro evento para passar ao construtor
        UUID aggregateId = UUID.fromString(events.get(0).aggregateId());
        Funcionario funcionario = AggregateRoot.fromEvents(
                events,
                () -> new Funcionario(aggregateId)
        );
        return Optional.of(funcionario);
    }*/

    @Override
    public Optional<Funcionario> findById(UUID id) {
        String aggregateIdString = id.toString();

        // 1. Tenta carregar snapshot
        Optional<AggregateSnapshot> snapshot = snapshotRepository.findLatest(aggregateIdString);

        if (snapshot.isPresent()) {
            // 2. Carrega apenas eventos após o snapshot
            long snapshotVersion = snapshot.get().version();
            List<DomainEvent> recentEvents = eventStore.loadFromVersion(
                    aggregateIdString, snapshotVersion
            );

            // 3. Reconstrói: snapshot + eventos recentes
            Funcionario funcionario = deserializeFromSnapshot(snapshot.get());
            for (DomainEvent event : recentEvents) {
                funcionario.replay(event);
            }
            return Optional.of(funcionario);

        } else {
            // Sem snapshot: carrega todos os eventos
            List<DomainEvent> events = eventStore.load(aggregateIdString);
            if (events.isEmpty()) return Optional.empty();

            // NOTA: Extraímos o aggregateId do primeiro evento para passar ao construtor
            UUID aggregateId = UUID.fromString(events.get(0).aggregateId());
            return Optional.of(AggregateRoot.fromEvents(
                    events,
                    () -> new Funcionario(aggregateId)
            ));
        }
    }

    /**
     * Verifica se existe funcionário com o CPF.
     * Usa a tabela 3NF para performance.
     */
    public boolean existsByCpf(String cpf) {
        String sql = "SELECT EXISTS(SELECT 1 FROM rh.funcionario WHERE cpf = ?)";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, cpf)
        );
    }

    private boolean shouldCreateSnapshot(long version) {
        return version > 0 && version % SNAPSHOT_INTERVAL == 0;
    }

    private void createSnapshot(Funcionario funcionario) {
        try {
            String state = objectMapper.writeValueAsString(funcionario.toSnapshot());

            snapshotRepository.save(new AggregateSnapshot(
                    UUIDv7.generate(),
                    funcionario.getId().toString(),
                    "Funcionario",
                    funcionario.getVersion(),
                    state,
                    Instant.now()
            ));
        } catch (Exception e) {
            log.warn("Erro criando snapshot", e);
        }
    }

    private Funcionario deserializeFromSnapshot(AggregateSnapshot snapshot) {
        try {
            FuncionarioSnapshot data = objectMapper.readValue(
                    snapshot.state(), FuncionarioSnapshot.class
            );
            return Funcionario.fromSnapshot(data, snapshot.version());
        } catch (Exception e) {
            throw new RuntimeException("Erro deserializando snapshot", e);
        }
    }
}