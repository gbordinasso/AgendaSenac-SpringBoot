package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByHorario(Horario horario);
}
