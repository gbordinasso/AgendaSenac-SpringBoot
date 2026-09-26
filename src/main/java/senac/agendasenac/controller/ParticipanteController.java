package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.entity.Participante;
import senac.agendasenac.service.ParticipanteService;

import java.util.List;

@RestController
@RequestMapping("/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @PostMapping
    public ResponseEntity<Participante> salvar(@RequestBody Participante participante) {

        Participante participanteSalvo = participanteService.salvar(participante);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(participanteSalvo);
    }

    @GetMapping
    public ResponseEntity<List<Participante>> listarTodos() {
        return ResponseEntity.ok(participanteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participante> buscarPorId(
            @PathVariable Long id) {

        return participanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participante> atualizar(@PathVariable Long id, @RequestBody Participante participante) {
        Participante participanteAtualizado = participanteService.atualizar(id, participante);

        return ResponseEntity.ok(participanteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        participanteService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}