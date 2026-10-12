package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.TipoUsuario;
import senac.agendasenac.entity.Usuario;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);
}
