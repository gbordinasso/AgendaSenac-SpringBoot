package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Participante;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    boolean existsByCpf(String cpf);
}
