// src/main/java/br/org/postalis/training/rh/shared/infrastructure/EmailService.java
package br.org.postalis.training.rh.shared.infrastructure;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void enviarBoasVindas(String email, String nome) {
        log.info("[EMAIL] Enviando boas-vindas para {} <{}>", nome, email);
        // Integrar com serviço de email real
    }

    public void enviarNotificacaoPromocao(String funcionarioId, String cargo, BigDecimal salario) {
        log.info("[EMAIL] Notificando promoção: {} -> {} (R$ {})",
                funcionarioId, cargo, salario);
    }

    public void notificarDesligamento(String funcionarioId, String motivo) {
        log.info("[EMAIL] Notificando RH sobre desligamento: {} - {}",
                funcionarioId, motivo);
    }

    public void enviarConfirmacaoFerias(String feriasId) {
        log.info("[EMAIL] Confirmando férias aprovadas: {}", feriasId);
    }

    public void enviarRejeicaoFerias(String feriasId, String motivo) {
        log.info("[EMAIL] Notificando rejeição de férias: {} - {}", feriasId, motivo);
    }
}