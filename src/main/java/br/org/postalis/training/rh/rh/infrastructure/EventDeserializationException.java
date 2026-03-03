package br.org.postalis.training.rh.rh.infrastructure;

public class EventDeserializationException extends RuntimeException {

    public EventDeserializationException(String eventType, int version, Throwable cause) {
        super(String.format(
                "Erro ao deserializar evento: type=%s, version=%d",
                eventType, version
        ), cause);
    }
}
