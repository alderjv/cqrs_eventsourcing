// src/main/java/br/org/postalis/training/rh/rh/api/FuncionarioController.java
package br.org.postalis.training.rh.rh.api;

import br.org.postalis.training.rh.rh.api.dto.FuncionarioResponse;
import br.org.postalis.training.rh.rh.application.command.ContratarFuncionarioCommand;
import br.org.postalis.training.rh.rh.application.command.DesligarFuncionarioCommand;
import br.org.postalis.training.rh.rh.application.command.PromoverFuncionarioCommand;
import br.org.postalis.training.rh.rh.application.handler.ContratarFuncionarioHandler;
import br.org.postalis.training.rh.rh.application.handler.DesligarFuncionarioHandler;
import br.org.postalis.training.rh.rh.application.handler.PromoverFuncionarioHandler;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioCacheService;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioQueryRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funcionarios")
@AllArgsConstructor
public class FuncionarioController {

    private final ContratarFuncionarioHandler contratarHandler;
    private final PromoverFuncionarioHandler promoverHandler;
    private final FuncionarioQueryRepository queryRepository;
    private final DesligarFuncionarioHandler desligarHandler;
    private final FuncionarioCacheService cacheService;

    @PostMapping
    public ResponseEntity<Map<String, String>> contratar(
            @Valid @RequestBody ContratarFuncionarioCommand command) {

        contratarHandler.handle(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Funcionário contratado com sucesso"));
    }

    @GetMapping
    public List<FuncionarioResponse> listar() {
        return queryRepository.findAllOrderByNome().stream()
                .map(FuncionarioResponse::from)
                .toList();
    }

    /*@GetMapping("/ativos")
    public List<FuncionarioResponse> listarAtivos() {
        return queryRepository.findByAtivoTrue().stream()
                .map(FuncionarioResponse::from)
                .toList();
    }*/

   /* @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable UUID id) {
        return queryRepository.findById(id)
                .map(FuncionarioResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/

   /* @GetMapping("/cpf/{cpf}")
    public ResponseEntity<FuncionarioResponse> buscarPorCpf(@PathVariable String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return queryRepository.findByCpf(cpfLimpo)
                .map(FuncionarioResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/

    @PutMapping("/{id}/promover")
    public ResponseEntity<Map<String, String>> promover(
            @PathVariable UUID id,
            @Valid @RequestBody PromoverFuncionarioCommand command) {

        // Garantir que o ID do path é usado
        var commandComId = new PromoverFuncionarioCommand(
                id,
                command.novoCargo(),
                command.novoSalario()
        );

        promoverHandler.handle(commandComId);

        return ResponseEntity.ok(Map.of("message", "Funcionário promovido com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desligar(
            @PathVariable UUID id,
            @Valid @RequestBody DesligarFuncionarioCommand command) {

        var commandComId = new DesligarFuncionarioCommand(id, command.motivo());

        desligarHandler.handle(commandComId);

        return ResponseEntity.ok(Map.of("message", "Funcionário desligado com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable UUID id) {
        return cacheService.findById(id)  // ← via cache
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ativos")
    public List<FuncionarioResponse> listarAtivos() {
        return cacheService.findAtivos();  // ← via cache
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<FuncionarioResponse> buscarPorCpf(@PathVariable String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return cacheService.findByCpf(cpfLimpo)  // ← via cache
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}