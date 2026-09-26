package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.entity.Horario;
import senac.agendasenac.service.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    public ResponseEntity<Horario> salvar(@RequestBody Horario horario) {
        Horario horarioSalvo = horarioService.salvar(horario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(horarioSalvo);
    }

    @GetMapping
    public ResponseEntity<List<Horario>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horario> buscarPorId(@PathVariable Long id) {
        return horarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horario> atualizar(@PathVariable Long id, @RequestBody Horario horario) {

        Horario horarioAtualizado = horarioService.atualizar(id, horario);

        return ResponseEntity.ok(horarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        horarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}