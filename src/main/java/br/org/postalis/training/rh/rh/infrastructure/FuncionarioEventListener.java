// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FuncionarioEventListener.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDesligado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioPromovido;
import br.org.postalis.training.rh.shared.infrastructure.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class FuncionarioEventListener {

    private final EmailService emailService;

    public FuncionarioEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void onFuncionarioContratado(FuncionarioContratado event) {
        // Enviar email de boas-vindas
        try {
            emailService.enviarBoasVindas(event.email(), event.nome());
        } catch (Exception e) {
            log.error("Erro enviando email de boas-vindas para {}",
                    event.email(), e);
            // Opção: salvar em tabela de retry
            // retryQueue.save(new RetryTask("email-boas-vindas", event));
        }
    }

    @Async
    @EventListener
    public void onFuncionarioPromovido(FuncionarioPromovido event) {
        // Notificar sobre promoção
        emailService.enviarNotificacaoPromocao(
                event.aggregateId(),
                event.cargoNovo(),
                event.salarioNovo()
        );
    }

    @Async
    @EventListener
    public void onFuncionarioDesligado(FuncionarioDesligado event) {
        // Notificar RH sobre desligamento
        emailService.notificarDesligamento(event.aggregateId(), event.motivo());
    }
}