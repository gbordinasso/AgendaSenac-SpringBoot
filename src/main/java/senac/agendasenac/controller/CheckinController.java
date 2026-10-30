package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.CheckinDTO;
import senac.agendasenac.entity.Checkin;
import senac.agendasenac.service.CheckinService;

import java.util.List;

@RestController
@RequestMapping("/checkins")
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @PostMapping
    public ResponseEntity<CheckinDTO> salvar(@RequestParam Long reservaId, @RequestParam String codigoAcesso) {
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
                .orElseGet(() -> ResponseEntity.notFound().build());
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