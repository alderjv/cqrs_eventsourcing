// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/ConcurrencyException.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

public class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String message) {
        super(message);
    }
}