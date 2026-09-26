package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.ReservaParticipante;
import senac.agendasenac.entity.ReservaParticipanteId;

public interface ReservaParticipanteRepository extends JpaRepository<ReservaParticipante, ReservaParticipanteId> {
}
