// src/main/java/br/org/postalis/training/rh/shared/domain/UUIDv7.java
package br.org.postalis.training.rh.shared.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Gerador de UUID v7 (ordenável por tempo).
 */
public final class UUIDv7 {

    private UUIDv7() {
    }

    public static UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}