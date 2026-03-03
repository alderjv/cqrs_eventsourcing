// src/main/java/br/org/postalis/training/rh/shared/infrastructure/messaging/DomainEventPublisher.java
package br.org.postalis.training.rh.shared.infrastructure.messaging;

import br.org.postalis.training.rh.shared.domain.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public DomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publica evento de forma assíncrona.
     */
    @Async
    public void publish(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }

    /**
     * Publica vários eventos.
     */
    @Async
    public void publishAll(Iterable<DomainEvent> events) {
        for (DomainEvent event : events) {
            eventPublisher.publishEvent(event);
        }
    }
}