package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvar(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorIdETipo(Long id, TipoUsuario tipoUsuario) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getTipoUsuario() == tipoUsuario);
    }

    public Usuario salvarPorTipo(Usuario usuario, TipoUsuario tipoUsuario) {
        usuario.setTipoUsuario(tipoUsuario);
        return salvar(usuario);
    }

    public Usuario atualizar(Long id, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuarioExistente.getEmail().equals(usuario.getEmail())
                && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        if (!usuarioExistente.getCpf().equals(usuario.getCpf())
                && usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setSenha(usuario.getSenha());
        usuarioExistente.setCpf(usuario.getCpf());
        usuarioExistente.setCurso(usuario.getCurso());
        usuarioExistente.setTipoUsuario(usuario.getTipoUsuario());

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario atualizarPorTipo(Long id, Usuario usuario, TipoUsuario tipoUsuario) {

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuarioExistente.getTipoUsuario() != tipoUsuario) {
            throw new RuntimeException("Usuário não pertence a este tipo");
        }

        usuario.setTipoUsuario(tipoUsuario);

        return atualizar(id, usuario);
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }

        usuarioRepository.deleteById(id);
    }

    public void excluirPorTipo(Long id, TipoUsuario tipoUsuario) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getTipoUsuario() != tipoUsuario) {
            throw new RuntimeException("Usuário não pertence a este tipo");
        }

        excluir(id);
    }
}