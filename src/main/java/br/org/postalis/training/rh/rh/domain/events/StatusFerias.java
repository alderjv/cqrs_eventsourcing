// src/main/java/br/org/postalis/training/rh/rh/domain/StatusFerias.java
package br.org.postalis.training.rh.rh.domain.events;

public enum StatusFerias {
    SOLICITADA,
    APROVADA,
    REJEITADA,
    EM_GOZO,
    CONCLUIDA;

    public boolean podeAprovar() {
        return this == SOLICITADA;
    }

    public boolean podeRejeitar() {
        return this == SOLICITADA;
    }

    public boolean podeIniciarGozo() {
        return this == APROVADA;
    }

    public boolean podeConcluir() {
        return this == EM_GOZO;
    }

    public boolean isFinalizado() {
        return this == REJEITADA || this == CONCLUIDA;
    }
}