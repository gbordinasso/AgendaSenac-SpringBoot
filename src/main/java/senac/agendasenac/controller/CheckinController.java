package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Checkin> salvar(@RequestBody Checkin checkin) {
        Checkin checkinSalvo = checkinService.salvar(checkin);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(checkinSalvo);
    }

    @GetMapping
    public ResponseEntity<List<Checkin>> listarTodos() {
        return ResponseEntity.ok(checkinService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checkin> buscarPorId(@PathVariable Long id) {
        return checkinService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checkin> atualizar(@PathVariable Long id, @RequestBody Checkin checkin) {
        Checkin checkinAtualizado = checkinService.atualizar(id, checkin);

        return ResponseEntity.ok(checkinAtualizado);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Checkin> cancelar(@PathVariable Long id) {
        Checkin checkinCancelado = checkinService.cancelar(id);

        return ResponseEntity.ok(checkinCancelado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        checkinService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}