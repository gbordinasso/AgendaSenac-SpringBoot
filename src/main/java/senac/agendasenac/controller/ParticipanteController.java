package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.ParticipanteDTO;
import senac.agendasenac.entity.Participante;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
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
    public ResponseEntity<ParticipanteDTO> salvar(@Valid @RequestBody ParticipanteDTO dto) {
        Participante participanteSalvo = participanteService.salvar(dto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(ParticipanteDTO.fromEntity(participanteSalvo));
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteDTO>> listarTodos() {
        List<ParticipanteDTO> participantes = participanteService
                .listarTodos()
                .stream()
                .map(ParticipanteDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteDTO> buscarPorId(@PathVariable Long id) {
        return participanteService.buscarPorId(id)
                .map(ParticipanteDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Participante não encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipanteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ParticipanteDTO dto) {
        Participante participanteAtualizado = participanteService.atualizar(id, dto.toEntity());
        return ResponseEntity.ok(ParticipanteDTO.fromEntity(participanteAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        participanteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}