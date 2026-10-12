package senac.agendasenac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Usuario> salvar(@RequestBody Usuario usuario) {

        Usuario usuarioSalvo = usuarioService.salvarPorTipo(
                usuario, TipoUsuario.ALUNO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(
                usuarioService.listarPorTipo(TipoUsuario.ALUNO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdETipo(id, TipoUsuario.ALUNO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        Usuario usuarioAtualizado = usuarioService.atualizarPorTipo(
                id, usuario, TipoUsuario.ALUNO
        );

        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        usuarioService.excluirPorTipo(id, TipoUsuario.ALUNO);

        return ResponseEntity.noContent().build();
    }
}