package senac.agendasenac.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.exception.NaoAutorizadoException;
import senac.agendasenac.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final String MENSAGEM_LOGIN_INVALIDO = "E-mail ou senha inválidos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario login(String email, String senha) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NaoAutorizadoException(MENSAGEM_LOGIN_INVALIDO));

        if (!senhaConfere(senha, usuario.getSenha())) {
            throw new NaoAutorizadoException(MENSAGEM_LOGIN_INVALIDO);
        }

        return usuario;
    }

    private boolean senhaConfere(String senhaDigitada, String senhaSalva) {
        return passwordEncoder.matches(senhaDigitada, senhaSalva);
    }
}