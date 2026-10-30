package senac.agendasenac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.entity.Usuario;

public record UsuarioDTO(

        Long id,

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail é inválido")
        String email,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "\\d{11}",
                message = "O CPF deve ter 11 números, sem pontos ou traços"
        )
        String cpf,

        @NotBlank(message = "O curso é obrigatório")
        String curso,

        TipoUsuario tipoUsuario

) {

    public Usuario toEntity() {
        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setCpf(cpf);
        usuario.setCurso(curso);

        return usuario;
    }

    public static UsuarioDTO fromEntity(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                null,
                null,
                usuario.getCurso(),
                usuario.getTipoUsuario()
        );
    }
}