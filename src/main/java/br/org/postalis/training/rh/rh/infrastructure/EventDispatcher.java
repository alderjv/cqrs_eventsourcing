package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratadoV1;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDesligado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioPromovido;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EventDispatcher {

    private final ObjectMapper objectMapper;
    private final Map<EventKey, Class<? extends DomainEvent>> registry;

    public EventDispatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.registry = buildRegistry();
    }

    /**
     * Registry mapeando (event_type, event_version) → Classe Java.
     *
     * IMPORTANTE:
     * - event_type é ESTÁVEL (não muda entre versões)
     * - Adicionar novas versões aqui quando criar eventos novos
     */
    private Map<EventKey, Class<? extends DomainEvent>> buildRegistry() {
        return Map.ofEntries(
                // FuncionarioContratado
                Map.entry(EventKey.of("FuncionarioContratado", 1),
                        FuncionarioContratadoV1.class),
                Map.entry(EventKey.of("FuncionarioContratado", 2),
                        FuncionarioContratado.class),

                // FuncionarioPromovido (se tiver versionamento)
                Map.entry(EventKey.of("FuncionarioPromovido", 1),
                        FuncionarioPromovido.class),

                // FuncionarioDesligado
                Map.entry(EventKey.of("FuncionarioDesligado", 1),
                        FuncionarioDesligado.class)

                // Adicionar novos eventos aqui...
        );
    }

    /**
     * Deserializa evento do banco para objeto tipado.
     *
     * @param eventType Nome estável do evento
     * @param version Versão do schema
     * @param data Payload JSON do evento
     * @return Evento tipado
     * @throws UnknownEventVersionException se versão não registrada
     */
    public DomainEvent deserialize(String eventType, int version, JsonNode data) {
        var key = EventKey.of(eventType, version);
        var eventClass = registry.get(key);

        if (eventClass == null) {
            throw new UnknownEventVersionException(eventType, version);
        }

        try {
            return objectMapper.treeToValue(data, eventClass);
        } catch (Exception e) {
            throw new EventDeserializationException(eventType, version, e);
        }
    }

    /**
     * Obtém a versão atual para um tipo de evento.
     * Usada ao persistir novos eventos.
     */
    public int getCurrentVersion(String eventType) {
        return registry.keySet().stream()
                .filter(k -> k.type().equals(eventType))
                .mapToInt(EventKey::version)
                .max()
                .orElse(1);
    }
}