// src/main/java/br/org/postalis/training/rh/rh/infrastructure/CacheInvalidationListener.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDesligado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioPromovido;
import br.org.postalis.training.rh.shared.infrastructure.CacheInvalidator;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationListener {

    private final CacheInvalidator cacheInvalidator;

    public CacheInvalidationListener(CacheInvalidator cacheInvalidator) {
        this.cacheInvalidator = cacheInvalidator;
    }

    @EventListener
    public void onFuncionarioContratado(FuncionarioContratado event) {
        cacheInvalidator.invalidateFuncionariosAtivos();
    }

    @EventListener
    public void onFuncionarioPromovido(FuncionarioPromovido event) {
        UUID id = UUID.fromString(event.aggregateId());
        cacheInvalidator.invalidateFuncionario(id);
    }

    @EventListener
    public void onFuncionarioDesligado(FuncionarioDesligado event) {
        UUID id = UUID.fromString(event.aggregateId());
        cacheInvalidator.invalidateFuncionario(id);
        cacheInvalidator.invalidateFuncionariosAtivos();
    }
}