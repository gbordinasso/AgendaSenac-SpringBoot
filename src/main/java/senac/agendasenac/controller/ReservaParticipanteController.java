package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.entity.ReservaParticipante;
import senac.agendasenac.entity.ReservaParticipanteId;
import senac.agendasenac.service.ReservaParticipanteService;

import java.util.List;

@RestController
@RequestMapping("/reservas-participantes")
public class ReservaParticipanteController {

    private final ReservaParticipanteService reservaParticipanteService;

    public ReservaParticipanteController(ReservaParticipanteService reservaParticipanteService) {
        this.reservaParticipanteService = reservaParticipanteService;
    }

    @PostMapping
    public ResponseEntity<ReservaParticipante> salvar(@RequestBody ReservaParticipante reservaParticipante) {
        ReservaParticipante participacaoSalva = reservaParticipanteService.salvar(reservaParticipante);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(participacaoSalva);
    }

    @GetMapping
    public ResponseEntity<List<ReservaParticipante>> listarTodos() {
        return ResponseEntity.ok(reservaParticipanteService.listarTodos());
    }

    @GetMapping("/{reservaId}/{participanteId}")
    public ResponseEntity<ReservaParticipante> buscarPorId(@PathVariable Long reservaId, @PathVariable Long participanteId) {
        ReservaParticipanteId id = criarId(reservaId, participanteId);

        return reservaParticipanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{reservaId}/{participanteId}")
    public ResponseEntity<ReservaParticipante> atualizar(@PathVariable Long reservaId, @PathVariable Long participanteId, @RequestBody ReservaParticipante reservaParticipante) {
        ReservaParticipanteId id = criarId(reservaId, participanteId);
        ReservaParticipante participacaoAtualizada = reservaParticipanteService.atualizar(id, reservaParticipante);

        return ResponseEntity.ok(participacaoAtualizada);
    }

    @DeleteMapping("/{reservaId}/{participanteId}")
    public ResponseEntity<Void> excluir(@PathVariable Long reservaId, @PathVariable Long participanteId) {
        ReservaParticipanteId id = criarId(reservaId, participanteId);
        reservaParticipanteService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    private ReservaParticipanteId criarId(Long reservaId, Long participanteId) {
        ReservaParticipanteId id = new ReservaParticipanteId();

        id.setReservaId(reservaId);
        id.setParticipanteId(participanteId);

        return id;
    }
}