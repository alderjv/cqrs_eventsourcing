// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/EventTypeRegistry.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDemitido;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDesligado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioPromovido;
import br.org.postalis.training.rh.funcionario.domain.events.SalarioAjustado;
import br.org.postalis.training.rh.rh.domain.events.FeriasAprovadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasConcluidaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasIniciadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasRejeitadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasSolicitadaEvent;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Mapa de tipos de eventos para deserialização.
 * <p>
 * NOTA: Esta é uma versão simplificada que não considera event_version.
 * Para produção com múltiplas versões de eventos, veja o Módulo 08.
 *
 * @see <a href="/Treinamento-Pratico-Backend/08-Event-Versioning">Event Versioning</a>
 */
@Component
public class EventTypeRegistry {

    private final Map<String, Class<? extends DomainEvent>> registry = new HashMap<>();

    public EventTypeRegistry() {
        // Registrar todos os eventos de Funcionario
        register("FuncionarioContratado", FuncionarioContratado.class);
        register("FuncionarioDemitido", FuncionarioDemitido.class);
        register("SalarioAjustado", SalarioAjustado.class);

        // Adicionar novos eventos conforme criados:
        register("FuncionarioPromovido", FuncionarioPromovido.class);
        register("FuncionarioDesligado", FuncionarioDesligado.class);

        // Eventos de férias
        register("FeriasSolicitada", FeriasSolicitadaEvent.class);
        register("FeriasAprovada", FeriasAprovadaEvent.class);
        register("FeriasRejeitada", FeriasRejeitadaEvent.class);
        register("FeriasIniciada", FeriasIniciadaEvent.class);
        register("FeriasConcluida", FeriasConcluidaEvent.class);
    }

    private void register(String eventType, Class<? extends DomainEvent> clazz) {
        registry.put(eventType, clazz);
    }

    public Class<? extends DomainEvent> getEventClass(String eventType) {
        Class<? extends DomainEvent> clazz = registry.get(eventType);
        if (clazz == null) {
            throw new IllegalArgumentException("Evento não registrado: " + eventType);
        }
        return clazz;
    }
}