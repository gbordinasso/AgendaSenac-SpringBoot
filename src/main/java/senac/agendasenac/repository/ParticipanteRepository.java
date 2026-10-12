package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Participante;

import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    boolean existsByCpf(String cpf);

    Optional<Participante> findByCpf(String cpf);
}
