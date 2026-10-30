package senac.agendasenac.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.repository.ReservaRepository;
import senac.agendasenac.repository.UsuarioRepository;
import senac.agendasenac.exception.ConflitoException;
import senac.agendasenac.exception.RecursoNaoEncontradoException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ConflitoException("E-mail já cadastrado");
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new ConflitoException("CPF já cadastrado");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!usuarioExistente.getEmail().equals(usuario.getEmail())
                && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ConflitoException("E-mail já cadastrado");
        }

        if (!usuarioExistente.getCpf().equals(usuario.getCpf())
                && usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new ConflitoException("CPF já cadastrado");
        }

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioExistente.setCpf(usuario.getCpf());
        usuarioExistente.setCurso(usuario.getCurso());
        usuarioExistente.setTipoUsuario(usuario.getTipoUsuario());

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario atualizarPorTipo(Long id, Usuario usuario, TipoUsuario tipoUsuario) {

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (usuarioExistente.getTipoUsuario() != tipoUsuario) {
            throw new RecursoNaoEncontradoException("Usuário não pertence a este tipo");
        }

        usuario.setTipoUsuario(tipoUsuario);

        return atualizar(id, usuario);
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }

        if (reservaRepository.existsByUsuario_Id(id)) {
            throw new ConflitoException("O usuário possui reservas e não pode ser excluído");
        }

        usuarioRepository.deleteById(id);
    }

    public void excluirPorTipo(Long id, TipoUsuario tipoUsuario) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (usuario.getTipoUsuario() != tipoUsuario) {
            throw new RecursoNaoEncontradoException("Usuário não pertence a este tipo");
        }

        excluir(id);
    }
}