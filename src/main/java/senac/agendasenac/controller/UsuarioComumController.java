package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.UsuarioDTO;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/usuarios/comum")
public class UsuarioComumController {

    private final UsuarioService usuarioService;

    public UsuarioComumController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvar (@Valid @RequestBody UsuarioDTO dto) {

        Usuario usuario = dto.toEntity();

        Usuario usuarioSalvo = usuarioService.salvarPorTipo(usuario, TipoUsuario.ALUNO);

        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromEntity(usuarioSalvo));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {

        List<UsuarioDTO> usuarios = usuarioService
                .listarPorTipo(TipoUsuario.ALUNO)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdETipo(id, TipoUsuario.ALUNO)
                .map(UsuarioDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        Usuario usuarioAtualizado = usuarioService.atualizarPorTipo(id, dto.toEntity(), TipoUsuario.ALUNO);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuarioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluirPorTipo(id, TipoUsuario.ALUNO);
        return ResponseEntity.noContent().build();
    }
}