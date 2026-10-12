package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.agendasenac.entity.Usuario;
import senac.agendasenac.entity.TipoUsuario;
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
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(
                usuarioService.listarPorTipo(TipoUsuario.ADMIN)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdETipo(id, TipoUsuario.ADMIN)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/alunos")
    public ResponseEntity<List<Usuario>> listarAlunos() {

        List<Usuario> alunos = usuarioService.listarPorTipo(TipoUsuario.ALUNO);

        return ResponseEntity.ok(alunos);
    }


    @GetMapping("/alunos/{id}")
    public ResponseEntity<Usuario> buscarAlunoPorId(@PathVariable Long id) {

        return usuarioService.buscarPorIdETipo(id, TipoUsuario.ALUNO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/alunos/{id}")
    public ResponseEntity<Usuario> atualizarAluno(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        Usuario alunoAtualizado = usuarioService.atualizarPorTipo(
                id,
                usuario,
                TipoUsuario.ALUNO
        );

        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> excluirAluno(@PathVariable Long id) {

        usuarioService.excluirPorTipo(id, TipoUsuario.ALUNO);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Usuario> salvar(@RequestBody Usuario usuario) {

        Usuario adminSalvo = usuarioService.salvarPorTipo(
                usuario,
                TipoUsuario.ADMIN
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        Usuario usuarioAtualizado = usuarioService.atualizarPorTipo(
                id, usuario, TipoUsuario.ADMIN
        );

        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        usuarioService.excluirPorTipo(id, TipoUsuario.ADMIN);

        return ResponseEntity.noContent().build();
    }
}