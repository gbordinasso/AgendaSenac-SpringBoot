package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.Reserva;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    boolean existsByReserva(Reserva reserva);
}
