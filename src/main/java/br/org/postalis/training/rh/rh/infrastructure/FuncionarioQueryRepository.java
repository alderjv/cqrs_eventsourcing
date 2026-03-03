// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FuncionarioQueryRepository.java
package br.org.postalis.training.rh.rh.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository JPA para QUERIES apenas.
 * Escrita é feita via Event Store + Projector.
 */
public interface FuncionarioQueryRepository
        extends JpaRepository<FuncionarioJpaEntity, UUID> {

    Optional<FuncionarioJpaEntity> findByCpf(String cpf);

    List<FuncionarioJpaEntity> findByAtivoTrue();

    List<FuncionarioJpaEntity> findByAtivoFalse();

    @Query("SELECT f FROM FuncionarioJpaEntity f WHERE f.cargo = :cargo AND f.ativo = true")
    List<FuncionarioJpaEntity> findByCargo(String cargo);

    @Query("SELECT f FROM FuncionarioJpaEntity f ORDER BY f.nome")
    List<FuncionarioJpaEntity> findAllOrderByNome();
}