package br.org.postalis.training.rh.rh.infrastructure;

public class UnknownEventVersionException extends RuntimeException {

    public UnknownEventVersionException(String eventType, int version) {
        super(String.format(
                "Versão desconhecida do evento: type=%s, version=%d. " +
                        "Verifique se o EventDispatcher está atualizado.",
                eventType, version
        ));
    }
}

