// src/test/java/br/org/postalis/training/rh/rh/application/handler/ContratarFuncionarioHandlerTest.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.rh.application.command.ContratarFuncionarioCommand;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ContratarFuncionarioHandlerTest {

    @Autowired
    private ContratarFuncionarioHandler handler;

    @Test
    void deveContratarFuncionario() {
        var command = new ContratarFuncionarioCommand(
                "006.272.641-23",
                "João Silva",
                "joao@empresa.com",
                "Desenvolvedor",
                new BigDecimal("5000")
        );

        // Não deve lançar exceção
        assertDoesNotThrow(() -> handler.handle(command));
    }

    @Test
    void naoDeveContratarCpfDuplicado() {
        var command = new ContratarFuncionarioCommand(
                "220.113.401-44",
                "João Silva",
                "joao@empresa.com",
                "Dev",
                new BigDecimal("5000")
        );

        handler.handle(command);

        // Tentar contratar novamente com mesmo CPF
        assertThrows(BusinessRuleException.class,
                () -> handler.handle(command));
    }
}