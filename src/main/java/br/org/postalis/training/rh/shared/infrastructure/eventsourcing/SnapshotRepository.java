// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/SnapshotRepository.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import java.util.Optional;

public interface SnapshotRepository {
    void save(AggregateSnapshot snapshot);

    Optional<AggregateSnapshot> findLatest(String aggregateId);
}