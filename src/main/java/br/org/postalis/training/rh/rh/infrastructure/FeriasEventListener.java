// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FeriasEventListener.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.domain.events.FeriasAprovadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasRejeitadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasSolicitadaEvent;
import br.org.postalis.training.rh.shared.infrastructure.EmailService;
import br.org.postalis.training.rh.shared.infrastructure.messaging.NotificacaoService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FeriasEventListener {

    private final EmailService emailService;
    private final NotificacaoService notificacaoService;

    @Async
    @EventListener
    public void onFeriasSolicitada(FeriasSolicitadaEvent event) {
        // Notificar gerente para aprovar
        notificacaoService.notificarGerenteNovasFerias(
                event.funcionarioId(),
                event.dataInicio(),
                event.dataFim()
        );
    }

    @Async
    @EventListener
    public void onFeriasAprovada(FeriasAprovadaEvent event) {
        // Notificar funcionário
        emailService.enviarConfirmacaoFerias(event.aggregateId());
    }

    @Async
    @EventListener
    public void onFeriasRejeitada(FeriasRejeitadaEvent event) {
        // Notificar funcionário com motivo
        emailService.enviarRejeicaoFerias(
                event.aggregateId(),
                event.motivo()
        );
    }
}