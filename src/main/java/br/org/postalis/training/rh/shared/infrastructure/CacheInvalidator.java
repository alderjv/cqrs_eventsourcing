// src/main/java/br/org/postalis/training/rh/shared/infrastructure/CacheInvalidator.java
package br.org.postalis.training.rh.shared.infrastructure;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidator {

    private static final Logger log = LoggerFactory.getLogger(CacheInvalidator.class);

    @CacheEvict(value = "funcionarios", key = "#id")
    public void invalidateFuncionario(UUID id) {
        log.debug("Cache invalidado: funcionario {}", id);
    }

    @CacheEvict(value = "funcionarios", key = "'cpf:' + #cpf")
    public void invalidateFuncionarioByCpf(String cpf) {
        log.debug("Cache invalidado: cpf {}", cpf);
    }

    @CacheEvict(value = "funcionarios", key = "'ativos'")
    public void invalidateFuncionariosAtivos() {
        log.debug("Cache invalidado: funcionarios ativos");
    }

    @CacheEvict(value = "funcionarios", allEntries = true)
    public void invalidateAllFuncionarios() {
        log.debug("Cache invalidado: todos os funcionarios");
    }
}