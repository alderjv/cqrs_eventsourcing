// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FuncionarioJpaEntity.java
package br.org.postalis.training.rh.rh.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

/**
 * Entity JPA para queries na tabela relacional.
 * NÃO usar para escrita - apenas leitura!
 */
@Entity
@Table(name = "funcionario", schema = "rh")
@Getter
public class FuncionarioJpaEntity {

    @Id
    private UUID id;

    private String cpf;
    private String nome;
    private String email;
    private String matricula;
    private String cargo;
    private BigDecimal salario;

    @Column(name = "data_admissao")
    private LocalDate dataAdimissao;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    private boolean ativo;


}