package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.entity.Quadra;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
import senac.agendasenac.service.QuadraService;

import java.util.List;

@RestController
@RequestMapping("/quadras")
public class QuadraController {

    private final QuadraService quadraService;

    public QuadraController(QuadraService quadraService) {
        this.quadraService = quadraService;
    }

    @PostMapping
    public ResponseEntity<Quadra> salvar(@Valid @RequestBody Quadra quadra) {
        Quadra quadraSalva = quadraService.salvar(quadra);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(quadraSalva);
    }

    @GetMapping
    public ResponseEntity<List<Quadra>> listarTodos() {
        return ResponseEntity.ok(quadraService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quadra> buscarPorId(@PathVariable Long id) {
        return quadraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quadra não encontrada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quadra> atualizar(@PathVariable Long id, @Valid @RequestBody Quadra quadra) {
        Quadra quadraAtualizada = quadraService.atualizar(id, quadra);

        return ResponseEntity.ok(quadraAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        quadraService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}