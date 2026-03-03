// src/main/java/br/org/postalis/training/rh/shared/api/AdminController.java
package br.org.postalis.training.rh.shared.api;

import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.ReplayService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReplayService replayService;

    public AdminController(ReplayService replayService) {
        this.replayService = replayService;
    }

    @PostMapping("/replay/funcionarios")
    public ResponseEntity<Map<String, String>> replayFuncionarios() {
        replayService.rebuildFuncionarioProjection();
        return ResponseEntity.ok(Map.of(
                "message", "Projeção de funcionários reconstruída"
        ));
    }

    @PostMapping("/replay/ferias")
    public ResponseEntity<Map<String, String>> replayFerias() {
        replayService.rebuildFeriasProjection();
        return ResponseEntity.ok(Map.of(
                "message", "Projeção de férias reconstruída"
        ));
    }

    @PostMapping("/replay/all")
    public ResponseEntity<Map<String, String>> replayAll() {
        replayService.rebuildFuncionarioProjection();
        replayService.rebuildFeriasProjection();
        return ResponseEntity.ok(Map.of(
                "message", "Todas as projeções reconstruídas"
        ));
    }
}