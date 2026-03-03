package br.org.postalis.training.rh.rh.infrastructure;

/**
 * Chave para identificar unicamente uma versão de evento.
 * Combina event_type (estável) + event_version (schema).
 */
public record EventKey(String type, int version) {

    public static EventKey of(String type, int version) {
        return new EventKey(type, version);
    }
}