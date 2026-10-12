package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.Reserva;

import java.util.Optional;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    boolean existsByReserva(Reserva reserva);


    Optional<Checkin> findByReserva(Reserva reserva);


    void deleteByReserva_Id(Long reservaId);
}
