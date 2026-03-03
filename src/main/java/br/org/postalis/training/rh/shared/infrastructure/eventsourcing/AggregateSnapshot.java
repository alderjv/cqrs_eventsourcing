// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/AggregateSnapshot.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import java.time.Instant;
import java.util.UUID;

public record AggregateSnapshot(
        UUID snapshotId,
        String aggregateId,
        String aggregateType,
        long version,
        String state,
        Instant createdAt
) {}