package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.StatusCheckin;

import java.util.Optional;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    boolean existsByReserva(Reserva reserva);

    boolean existsByReserva_IdAndStatus(Long reservaId, StatusCheckin status);

    Optional<Checkin> findByReserva(Reserva reserva);

    void deleteByReserva_Id(Long reservaId);
}
