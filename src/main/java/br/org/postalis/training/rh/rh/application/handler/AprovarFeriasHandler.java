// src/main/java/br/org/postalis/training/rh/rh/application/handler/AprovarFeriasHandler.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.rh.application.command.AprovarFeriasCommand;
import br.org.postalis.training.rh.rh.domain.Ferias;
import br.org.postalis.training.rh.rh.infrastructure.FeriasRepository;
import br.org.postalis.training.rh.shared.application.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AprovarFeriasHandler implements CommandHandler<AprovarFeriasCommand> {

    private final FeriasRepository repository;

    public AprovarFeriasHandler(FeriasRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void handle(AprovarFeriasCommand command) {
        Ferias ferias = repository.findById(command.feriasId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Férias não encontradas: " + command.feriasId()));

        ferias.aprovar(command.aprovadoPor(), command.observacao());

        repository.save(ferias);
    }
}