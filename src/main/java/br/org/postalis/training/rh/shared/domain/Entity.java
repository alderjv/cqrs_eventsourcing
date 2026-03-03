// src/main/java/br/org/postalis/training/rh/shared/domain/Entity.java
package br.org.postalis.training.rh.shared.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Classe base para todas as Entities do domínio.
 * <p>
 * No contexto de DDD, uma Entity é um objeto com identidade única
 * e contínua ao longo do tempo. Igualdade é baseada APENAS no ID.
 * <p>
 * IMPORTANTE: Não possui construtor vazio. O ID é final e imutável.
 * Para entidades JPA que precisam de construtor vazio, use JpaEntity.
 */
public abstract class Entity {

    private final UUID id;

    /**
     * Construtor protegido - entidades são criadas via métodos de negócio.
     *
     * @param id identificador único (não pode ser null)
     */
    protected Entity(UUID id) {
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo");
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}