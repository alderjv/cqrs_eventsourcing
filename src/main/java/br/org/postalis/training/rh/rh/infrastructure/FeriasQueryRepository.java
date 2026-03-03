// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FeriasQueryRepository.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeriasQueryRepository extends JpaRepository<FeriasJpaEntity, UUID> {

    List<FeriasJpaEntity> findByFuncionarioId(UUID funcionarioId);

    List<FeriasJpaEntity> findByStatus(StatusFerias status);

    List<FeriasJpaEntity> findByFuncionarioIdAndStatus(UUID funcionarioId, StatusFerias status);

    @Query("""
            SELECT f FROM FeriasJpaEntity f
            WHERE f.status = 'SOLICITADA'
            ORDER BY f.dataInicio
            """)
    List<FeriasJpaEntity> findPendentesAprovacao();

    @Query("""
            SELECT f FROM FeriasJpaEntity f
            WHERE f.funcionarioId = :funcionarioId
            AND f.status IN ('SOLICITADA', 'APROVADA', 'EM_GOZO')
            """)
    List<FeriasJpaEntity> findAtivasByFuncionario(UUID funcionarioId);

    @Query("""
            SELECT f FROM FeriasJpaEntity f
            WHERE f.status = 'APROVADA'
            AND f.dataInicio <= :data
            """)
    List<FeriasJpaEntity> findAprovadasParaIniciar(LocalDate data);
}