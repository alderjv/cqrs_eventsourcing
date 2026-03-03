// src/main/java/br/org/postalis/training/rh/funcionario/domain/events/FuncionarioEvent.java
package br.org.postalis.training.rh.funcionario.domain.events;

import br.org.postalis.training.rh.shared.domain.DomainEvent;

/**
 * Interface selada que agrupa todos os eventos do aggregate Funcionario.
 * <p>
 * Garante:
 * - Type safety: compilador valida exaustividade em pattern matching
 * - Evolução controlada: novos eventos devem ser adicionados ao permits
 * - Documentação viva: todos os eventos possíveis estão listados aqui
 */
public sealed interface FuncionarioEvent extends DomainEvent
        permits FuncionarioContratadoV1,
        FuncionarioContratado,
        FuncionarioDemitido,
        SalarioAjustado,
       // FuncionarioPromovidoV1,
        FuncionarioPromovido,
        FuncionarioDesligado {
}