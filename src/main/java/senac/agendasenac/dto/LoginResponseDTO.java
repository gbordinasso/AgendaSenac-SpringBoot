package senac.agendasenac.dto;

import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.entity.Usuario;

// O que o app recebe ao entrar, os dados do usuário mais o token
public record LoginResponseDTO(
        Long id,
        String nome,
        String email,
        String curso,
        TipoUsuario tipoUsuario,
        String token
) {

    public static LoginResponseDTO of(Usuario usuario, String token) {
        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCurso(),
                usuario.getTipoUsuario(),
                token
        );
    }
}
