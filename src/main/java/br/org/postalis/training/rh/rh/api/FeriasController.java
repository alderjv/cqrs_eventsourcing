// src/main/java/br/org/postalis/training/rh/rh/api/FeriasController.java
package br.org.postalis.training.rh.rh.api;

import br.org.postalis.training.rh.rh.api.dto.FeriasResponse;
import br.org.postalis.training.rh.rh.application.command.AprovarFeriasCommand;
import br.org.postalis.training.rh.rh.application.command.RejeitarFeriasCommand;
import br.org.postalis.training.rh.rh.application.command.SolicitarFeriasCommand;
import br.org.postalis.training.rh.rh.application.handler.AprovarFeriasHandler;
import br.org.postalis.training.rh.rh.application.handler.RejeitarFeriasHandler;
import br.org.postalis.training.rh.rh.application.handler.SolicitarFeriasHandler;
import br.org.postalis.training.rh.rh.domain.Ferias;
import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import br.org.postalis.training.rh.rh.infrastructure.FeriasQueryRepository;
import br.org.postalis.training.rh.rh.infrastructure.FeriasRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ferias")
public class FeriasController {

    private final SolicitarFeriasHandler solicitarHandler;
    private final AprovarFeriasHandler aprovarHandler;
    private final RejeitarFeriasHandler rejeitarHandler;
    private final FeriasRepository repository;
    private final FeriasQueryRepository queryRepository;

    public FeriasController(
            SolicitarFeriasHandler solicitarHandler,
            AprovarFeriasHandler aprovarHandler,
            RejeitarFeriasHandler rejeitarHandler,
            FeriasRepository repository,
            FeriasQueryRepository queryRepository) {
        this.solicitarHandler = solicitarHandler;
        this.aprovarHandler = aprovarHandler;
        this.rejeitarHandler = rejeitarHandler;
        this.repository = repository;
        this.queryRepository = queryRepository;
    }

    // === COMANDOS (escrita) ===

    @PostMapping
    public ResponseEntity<Map<String, Object>> solicitar(
            @Valid @RequestBody SolicitarFeriasCommand command) {

        UUID id = solicitarHandler.handleReturningId(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Férias solicitadas com sucesso",
                        "id", id.toString()
                ));
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<Map<String, String>> aprovar(
            @PathVariable UUID id,
            @Valid @RequestBody AprovarFeriasCommand command) {

        var cmd = new AprovarFeriasCommand(id, command.aprovadoPor(), command.observacao());
        aprovarHandler.handle(cmd);

        return ResponseEntity.ok(Map.of("message", "Férias aprovadas"));
    }

    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<Map<String, String>> rejeitar(
            @PathVariable UUID id,
            @Valid @RequestBody RejeitarFeriasCommand command) {

        var cmd = new RejeitarFeriasCommand(id, command.rejeitadoPor(), command.motivo());
        rejeitarHandler.handle(cmd);

        return ResponseEntity.ok(Map.of("message", "Férias rejeitadas"));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Map<String, String>> iniciarGozo(@PathVariable UUID id) {
        Ferias ferias = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Férias não encontradas"));

        ferias.iniciarGozo();
        repository.save(ferias);

        return ResponseEntity.ok(Map.of("message", "Gozo de férias iniciado"));
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<Map<String, String>> concluir(@PathVariable UUID id) {
        Ferias ferias = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Férias não encontradas"));

        ferias.concluir();
        repository.save(ferias);

        return ResponseEntity.ok(Map.of("message", "Férias concluídas"));
    }

    // === QUERIES (leitura) ===

    @GetMapping
    public List<FeriasResponse> listar() {
        return queryRepository.findAll().stream()
                .map(FeriasResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeriasResponse> buscarPorId(@PathVariable UUID id) {
        return queryRepository.findById(id)
                .map(FeriasResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public List<FeriasResponse> buscarPorFuncionario(@PathVariable UUID funcionarioId) {
        return queryRepository.findByFuncionarioId(funcionarioId).stream()
                .map(FeriasResponse::from)
                .toList();
    }

    @GetMapping("/pendentes")
    public List<FeriasResponse> listarPendentes() {
        return queryRepository.findPendentesAprovacao().stream()
                .map(FeriasResponse::from)
                .toList();
    }

    @GetMapping("/status/{status}")
    public List<FeriasResponse> buscarPorStatus(@PathVariable StatusFerias status) {
        return queryRepository.findByStatus(status).stream()
                .map(FeriasResponse::from)
                .toList();
    }
}