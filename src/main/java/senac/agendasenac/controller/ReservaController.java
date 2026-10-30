package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.ParticipanteDTO;
import senac.agendasenac.dto.ReservaResponseDTO;
import senac.agendasenac.entity.Participante;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
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
    public ResponseEntity<ReservaResponseDTO> salvar(@RequestBody Reserva reserva) {
        Reserva reservaSalva = reservaService.salvar(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservaResponseDTO.fromEntity(reservaSalva));
    }
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodos() {
        List<ReservaResponseDTO> reservas = reservaService
                .listarTodos()
                .stream()
                .map(ReservaResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id)
                .map(ReservaResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        reservaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participantes")
    public ResponseEntity<Void> adicionarParticipante(@PathVariable Long id, @Valid @RequestBody ParticipanteDTO dto) {
        reservaService.adicionarParticipante(id, dto.nome(), dto.cpf());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}