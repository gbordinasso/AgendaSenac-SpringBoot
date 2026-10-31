package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.ParticipanteDTO;
import senac.agendasenac.dto.ReservaResponseDTO;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
import senac.agendasenac.security.UsuarioLogado;
import senac.agendasenac.service.ReservaService;
import senac.agendasenac.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final UsuarioLogado usuarioLogado;

    public ReservaController(
            ReservaService reservaService,
            UsuarioService usuarioService,
            UsuarioLogado usuarioLogado) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.usuarioLogado = usuarioLogado;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> salvar(@RequestBody Reserva reserva) {

        // O aluno sempre reserva no próprio nome, o admin pode reservar para um aluno informando o usuário, se não informar, reserva para si
        Long donoId = usuarioLogado.getId();

        if (usuarioLogado.isAdmin()
                && reserva.getUsuario() != null
                && reserva.getUsuario().getId() != null) {
            donoId = reserva.getUsuario().getId();
        }

        Usuario dono = usuarioService.buscarPorId(donoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        reserva.setUsuario(dono);

        Reserva reservaSalva = reservaService.salvar(reserva);

        return ResponseEntity.status(HttpStatus.CREATED).body(ReservaResponseDTO.fromEntity(reservaSalva));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodos() {
        List<Reserva> reservas = usuarioLogado.isAdmin()
                ? reservaService.listarTodos()
                : reservaService.listarPorUsuario(usuarioLogado.getId());

        List<ReservaResponseDTO> resposta = reservas
                .stream()
                .map(ReservaResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        Reserva reserva = buscarReservaDoUsuario(id);

        return ResponseEntity.ok(ReservaResponseDTO.fromEntity(reserva));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        buscarReservaDoUsuario(id);

        reservaService.cancelar(id);

        return ResponseEntity.noContent().build();
    }

    // Só o admin chega aqui (a SecurityConfig barra o aluno)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        reservaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participantes")
    public ResponseEntity<Void> adicionarParticipante(@PathVariable Long id, @Valid @RequestBody ParticipanteDTO dto) {
        buscarReservaDoUsuario(id);

        reservaService.adicionarParticipante(id, dto.nome(), dto.cpf());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Busca a reserva e confere se quem pediu é o dono ou admin, Se não for é 403
    private Reserva buscarReservaDoUsuario(Long id) {
        Reserva reserva = reservaService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));

        usuarioLogado.exigirDonoOuAdmin(reserva.getUsuario().getId());

        return reserva;
    }
}