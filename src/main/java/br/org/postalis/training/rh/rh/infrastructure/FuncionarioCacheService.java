// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FuncionarioCacheService.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.api.dto.FuncionarioResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FuncionarioCacheService {

    private final FuncionarioQueryRepository queryRepository;

    @Cacheable(value = "funcionarios", key = "#id")
    public Optional<FuncionarioResponse> findById(UUID id) {
        return queryRepository.findById(id)
                .map(FuncionarioResponse::from);
    }

    @Cacheable(value = "funcionarios", key = "'cpf:' + #cpf")
    public Optional<FuncionarioResponse> findByCpf(String cpf) {
        return queryRepository.findByCpf(cpf)
                .map(FuncionarioResponse::from);
    }

    @Cacheable(value = "funcionarios", key = "'ativos'")
    public List<FuncionarioResponse> findAtivos() {
        return queryRepository.findByAtivoTrue().stream()
                .map(FuncionarioResponse::from)
                .toList();
    }

    @Cacheable(value = "funcionarios", key = "'cargo:' + #cargo")
    public List<FuncionarioResponse> findByCargo(String cargo) {
        return queryRepository.findByCargo(cargo).stream()
                .map(FuncionarioResponse::from)
                .toList();
    }

}