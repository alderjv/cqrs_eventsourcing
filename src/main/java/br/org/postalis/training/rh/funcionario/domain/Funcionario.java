// src/main/java/br/org/postalis/training/rh/funcionario/domain/Funcionario.java
package br.org.postalis.training.rh.funcionario.domain;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratadoV1;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDemitido;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDesligado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioEvent;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioPromovido;
import br.org.postalis.training.rh.funcionario.domain.events.SalarioAjustado;
import br.org.postalis.training.rh.rh.domain.FuncionarioSnapshot;
import br.org.postalis.training.rh.shared.domain.AggregateRoot;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import br.org.postalis.training.rh.shared.domain.Cpf;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import br.org.postalis.training.rh.shared.domain.Email;
import br.org.postalis.training.rh.shared.domain.Matricula;
import br.org.postalis.training.rh.shared.domain.UUIDv7;
import br.org.postalis.training.rh.shared.infrastructure.EmailService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * Aggregate Root para Funcionário.
 * <p>
 * Por enquanto, apenas contratação.
 * Promoção e desligamento serão adicionados depois.
 * <p>
 * NOTA: O ID é herdado de Entity via AggregateRoot.
 * Não declaramos campo id aqui - usamos getId() da superclasse.
 */
@Log4j2
@Getter
public class Funcionario extends AggregateRoot {

    // Estado do aggregate (ID herdado de Entity)
    private Cpf cpf;
    private String nome;
    private Email email;
    private Matricula matricula;
    private String cargo;
    private BigDecimal salario;
    private LocalDate dataAdmissao;
    private boolean ativo;
    private LocalDate dataDesligamento;

    @Autowired
    private EmailService emailService;

    /**
     * Construtor protegido - recebe UUID obrigatório.
     * <p>
     * Usado por:
     * - Factory method contratar() para novos funcionários
     * - fromEvents() para reconstrução a partir de eventos
     */
    public Funcionario(UUID id) {
        super(id);
    }

    // ========================================
    // FACTORY METHOD
    // ========================================

    /**
     * Admite um novo funcionário.
     * <p>
     * Esta é a ÚNICA forma de criar um Funcionário.
     */
    public static Funcionario contratar(
            Cpf cpf,
            String nome,
            Email email,
            Matricula matricula,
            String cargo,
            BigDecimal salario) {

        // INVARIANTE: Dados básicos obrigatórios
        Objects.requireNonNull(cpf, "CPF é obrigatório");
        Objects.requireNonNull(nome, "Nome é obrigatório");
        Objects.requireNonNull(email, "Email é obrigatório");
        Objects.requireNonNull(matricula, "Matrícula é obrigatória");

        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException("Cargo é obrigatório");
        }

        // INVARIANTE: Salário deve ser positivo
        if (salario == null || salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salário deve ser positivo");
        }

        // Gera ID ANTES de criar instância
        UUID funcionarioId = UUIDv7.generate();
        Funcionario funcionario = new Funcionario(funcionarioId);

        // Gerar evento com todos os dados necessários
        funcionario.raise(new FuncionarioContratado(
                UUIDv7.generate(),      // eventId
                Instant.now(),           // occurredOn
                funcionarioId,           // funcionarioId (mesmo do construtor)
                matricula.getValor(),    // matricula
                nome,                    // nome
                cpf.getValor(),          // cpf
                email.getValor(),        // email
                LocalDate.now(),         // dataAdmissao
                salario,                 // salario
                cargo,                   // cargo

                null                     // metadata (opcional)
        ));

        return funcionario;
    }

    // ========================================
    // APPLY (reconstrução de estado)
    // ========================================

    @Override
    protected void apply(DomainEvent event) {
        // Pattern matching com sealed interface - compilador garante exaustividade!
        if (event instanceof FuncionarioEvent fe) {
            switch (fe) {
                case FuncionarioContratado e -> on(e);
                case FuncionarioDemitido e -> on(e);
                case SalarioAjustado e -> on(e);
                case FuncionarioPromovido e -> on(e);
                case FuncionarioDesligado e -> on(e);
                default -> throw new IllegalArgumentException("Evento desconhecido: " + event.eventType());
            }
        }
    }

    @Async
    @EventListener
    private void on(FuncionarioContratado e) {
        // NOTA: ID já está definido na superclasse via construtor
        // Não redefinimos - apenas aplicamos o estado mutável
        this.cpf = Cpf.of(e.cpf());
        this.nome = e.nome();
        this.email = Email.of(e.email());
        this.matricula = Matricula.of(e.matricula());
        this.cargo = e.cargo();
        this.salario = e.salario();
        this.dataAdmissao = e.dataAdmissao();
        this.ativo = true;

    }

    private void on(FuncionarioDemitido e) {
        this.ativo = false;
    }

    private void on(SalarioAjustado e) {
        this.salario = e.salarioNovo();
    }

    /**
     * Promove o funcionário para novo cargo com novo salário.
     */
    public void promover(String novoCargo, BigDecimal novoSalario) {

        // INVARIANTE: Deve estar ativo
        verificarAtivo("Funcionário inativo não pode ser promovido");

        // INVARIANTE: Cargo obrigatório
        if (novoCargo == null || novoCargo.isBlank()) {
            throw new IllegalArgumentException("Novo cargo é obrigatório");
        }

        // INVARIANTE: Salário positivo
        if (novoSalario == null || novoSalario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Novo salário deve ser positivo");
        }

        if (this.salario == null) {
            throw new IllegalArgumentException("Salario original nulo");
        }

        // INVARIANTE: Salário deve aumentar
        if (novoSalario.compareTo(this.salario) <= 0) {
            throw new BusinessRuleException(
                    "Novo salário (" + novoSalario +
                            ") deve ser maior que o atual (" + this.salario + ")"
            );
        }

        // Gerar evento
        FuncionarioPromovido event = FuncionarioPromovido.create(
                this.getId().toString(),
                (int) getVersion(),
                this.cargo,
                novoCargo,
                this.salario,
                novoSalario);

        // Aplicar evento
        //apply(event);
        //addUncommittedEvent(event);
        raise(event);

    }

    /**
     * Desliga o funcionário.
     */
    public void desligar(String motivo) {
        // INVARIANTE: Deve estar ativo
        verificarAtivo("Funcionário já está desligado");

        // INVARIANTE: Motivo obrigatório
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo do desligamento é obrigatório");
        }

        // INVARIANTE: Motivo com tamanho mínimo
        if (motivo.length() < 10) {
            throw new IllegalArgumentException(
                    "Motivo deve ter pelo menos 10 caracteres"
            );
        }

        // Gerar evento
        FuncionarioDesligado event = FuncionarioDesligado.create(
                this.getId().toString(),
                (int) getVersion(),
                motivo
        );

        // Aplicar evento
        //apply(event);
        //addUncommittedEvent(event);
        raise(event);
    }

    // Adicionar handler de evento
    private void on(FuncionarioPromovido event) {
        this.cargo = event.cargoNovo();
        this.salario = event.salarioNovo();
    }

    // Adicionar handler de evento
    private void on(FuncionarioDesligado event) {
        this.ativo = false;
        this.dataDesligamento = event.dataDesligamento();
    }

    public LocalDate getDataDesligamento() {
        return dataDesligamento;
    }

    // === Métodos Auxiliares ===

    private void verificarAtivo(String mensagemErro) {
        if (!this.ativo) {
            throw new BusinessRuleException(mensagemErro);
        }
    }

    // ========================================
    // GETTERS (estado atual - read-only)
    // ========================================
    // não precisa, usado @Getter
    // getId() herdado de Entity - não precisa redefinir


    /**
     * Cria DTO para snapshot.
     */
    public FuncionarioSnapshot toSnapshot() {
        return new FuncionarioSnapshot(
                getId(),
                cpf.getValor(),
                nome,
                email.getValor(),
                matricula.getValor(),
                cargo,
                salario,
                dataAdmissao,
                dataDesligamento,
                ativo
        );
    }

    public void replay(DomainEvent event) {
        apply(event);
        incrementVersion();
    }

    /**
     * Reconstrói Aggregate a partir de snapshot.
     * NOTA: O ID é passado ao construtor (herdado de Entity).
     */
    public static Funcionario fromSnapshot(FuncionarioSnapshot snapshot, long version) {
        Funcionario funcionario = new Funcionario(snapshot.id());
        // ID já definido no construtor - apenas aplicamos o estado mutável
        funcionario.cpf = Cpf.of(snapshot.cpf());
        funcionario.nome = snapshot.nome();
        funcionario.email = Email.of(snapshot.email());
        funcionario.matricula = Matricula.of(snapshot.matricula());
        funcionario.cargo = snapshot.cargo();
        funcionario.salario = snapshot.salario();
        funcionario.dataAdmissao = snapshot.dataAdmissao();
        funcionario.dataDesligamento = snapshot.dataDesligamento();
        funcionario.ativo = snapshot.ativo();
        funcionario.setVersion(version);
        return funcionario;
    }

}