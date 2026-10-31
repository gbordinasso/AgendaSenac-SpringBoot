package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.CheckinDTO;
import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
import senac.agendasenac.security.UsuarioLogado;
import senac.agendasenac.service.CheckinService;
import senac.agendasenac.service.ReservaService;

import java.util.List;

@RestController
@RequestMapping("/checkins")
public class CheckinController {

    private final CheckinService checkinService;
    private final ReservaService reservaService;
    private final UsuarioLogado usuarioLogado;

    public CheckinController(
            CheckinService checkinService,
            ReservaService reservaService,
            UsuarioLogado usuarioLogado) {
        this.checkinService = checkinService;
        this.reservaService = reservaService;
        this.usuarioLogado = usuarioLogado;
    }

    @PostMapping
    public ResponseEntity<CheckinDTO> salvar(@RequestParam Long reservaId, @RequestParam String codigoAcesso) {

        // O aluno só faz check-in da própria reserva, o admin faz de qualquer uma
        Reserva reserva = reservaService.buscarPorId(reservaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));

        usuarioLogado.exigirDonoOuAdmin(reserva.getUsuario().getId());

        Checkin checkinSalvo = checkinService.salvar(reservaId, codigoAcesso);
        return ResponseEntity.status(HttpStatus.CREATED).body(CheckinDTO.fromEntity(checkinSalvo));
    }

    @GetMapping
    public ResponseEntity<List<CheckinDTO>> listarTodos() {
        List<CheckinDTO> checkins = checkinService.listarTodos()
                .stream()
                .map(CheckinDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(checkins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinDTO> buscarPorId(@PathVariable Long id) {
        return checkinService.buscarPorId(id)
                .map(CheckinDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Check-in não encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckinDTO> atualizar(@PathVariable Long id, @RequestBody Checkin checkin) {
        Checkin checkinAtualizado = checkinService.atualizar(id, checkin);
        return ResponseEntity.ok(CheckinDTO.fromEntity(checkinAtualizado));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CheckinDTO> cancelar(@PathVariable Long id) {
        Checkin checkinCancelado = checkinService.cancelar(id);
        return ResponseEntity.ok(CheckinDTO.fromEntity(checkinCancelado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        checkinService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}