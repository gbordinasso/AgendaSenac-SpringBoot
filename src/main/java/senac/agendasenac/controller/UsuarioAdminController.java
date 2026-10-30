package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.dto.UsuarioDTO;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
import senac.agendasenac.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/usuarios/admin")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;
    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> administradores = usuarioService
                .listarPorTipo(TipoUsuario.ADMIN)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(administradores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdETipo(id, TipoUsuario.ADMIN)
                .map(UsuarioDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador não encontrado"));
    }

    @GetMapping("/alunos")
    public ResponseEntity<List<UsuarioDTO>> listarAlunos() {
        List<UsuarioDTO> alunos = usuarioService
                .listarPorTipo(TipoUsuario.ALUNO)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(alunos);
    }


    @GetMapping("/alunos/{id}")
    public ResponseEntity<UsuarioDTO> buscarAlunoPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdETipo(id, TipoUsuario.ALUNO)
                .map(UsuarioDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));
    }

    @PutMapping("/alunos/{id}")
    public ResponseEntity<UsuarioDTO> atualizarAluno(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        Usuario alunoAtualizado = usuarioService.atualizarPorTipo(id, dto.toEntity(), TipoUsuario.ALUNO);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(alunoAtualizado));
    }

    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> excluirAluno(@PathVariable Long id) {
        usuarioService.excluirPorTipo(id, TipoUsuario.ALUNO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvar(@Valid @RequestBody UsuarioDTO dto) {
        Usuario adminSalvo = usuarioService.salvarPorTipo(dto.toEntity(), TipoUsuario.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromEntity(adminSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        Usuario adminAtualizado = usuarioService.atualizarPorTipo(id, dto.toEntity(), TipoUsuario.ADMIN);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(adminAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluirPorTipo(id, TipoUsuario.ADMIN);
        return ResponseEntity.noContent().build();
    }
}