// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FeriasRepository.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.domain.Ferias;
import br.org.postalis.training.rh.shared.domain.AggregateRepository;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.EventStore;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FeriasRepository implements AggregateRepository<Ferias> {

    private final EventStore eventStore;
    private final FeriasProjector projector;

    public FeriasRepository(EventStore eventStore, FeriasProjector projector) {
        this.eventStore = eventStore;
        this.projector = projector;
    }

    @Override
    @Transactional
    public void save(Ferias ferias) {
        List<DomainEvent> events = ferias.getUncommittedEvents();

        if (events.isEmpty()) {
            return;
        }

        // 1. Persistir eventos
        eventStore.save(
                ferias.getId().toString(),
                "Ferias",
                events,
                ferias.getVersion() - events.size()
        );

        // 2. Projetar (mesma transação)
        for (DomainEvent event : events) {
            projector.project(event);
        }

        // 3. Limpar uncommitted
        ferias.markEventsAsCommitted();
    }

    @Override
    public Optional<Ferias> findById(UUID id) {
        List<DomainEvent> events = eventStore.load(id.toString());

        if (events.isEmpty()) {
            return Optional.empty();
        }

        Ferias ferias = Ferias.fromEvents(events, () -> new Ferias(id));
        return Optional.of(ferias);
    }
}