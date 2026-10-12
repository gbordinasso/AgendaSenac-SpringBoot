package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.entity.Participante;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.service.ReservaService;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Reserva> salvar(@RequestBody Reserva reserva) {
        Reserva reservaSalva = reservaService.salvar(reserva);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservaSalva);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodos() {
        return ResponseEntity.ok(reservaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Reserva> concluir(@PathVariable Long id) {
        Reserva reservaConcluida = reservaService.concluir(id);

        return ResponseEntity.ok(reservaConcluida);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        reservaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participantes")
    public ResponseEntity<?> adicionarParticipante(@PathVariable Long id, @RequestBody Participante participante) {

        reservaService.adicionarParticipante(id, participante.getNome(), participante.getCpf());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}