package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.StatusReserva;

import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByHorarioAndStatus(Horario horario, StatusReserva status);

    boolean existsByCodigoAcesso(String codigoAcesso);

    Optional<Reserva> findByHorario(Horario horario);

    boolean existsByUsuario_Id(Long usuarioId);

    boolean existsByHorario_Quadra_Id(Long quadraId);
}
