// src/main/java/br/org/postalis/training/rh/shared/domain/event/projection/RelationalProjector.java
package br.org.postalis.training.rh.shared.domain.event.projection;

/**
 * Projector para Read Models relacionais normalizados (3NF).
 * <p>
 * Ideal para:
 * - Business Intelligence (BI)
 * - Relatórios gerenciais
 * - Auditoria estruturada
 * - Integração com ETL (Power BI, Tableau)
 */
public interface RelationalProjector extends Projector {

    /**
     * Indica que esta projeção segue a Terceira Forma Normal (3NF).
     */
    default boolean isNormalized() {
        return true;
    }
}