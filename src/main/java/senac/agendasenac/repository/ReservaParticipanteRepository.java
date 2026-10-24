package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.ReservaParticipante;
import senac.agendasenac.entity.ReservaParticipanteId;

import java.util.List;

public interface ReservaParticipanteRepository extends JpaRepository<ReservaParticipante, ReservaParticipanteId> {

    void deleteByReserva_Id(Long reservaId);

    void deleteByParticipante_Id(Long participanteId);

    List<ReservaParticipante> findByReserva_Id(Long reservaId);

    boolean existsByParticipante_Id(Long participanteId);
}