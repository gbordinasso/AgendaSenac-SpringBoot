package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Quadra;

import java.time.LocalDate;
import java.time.LocalTime;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    boolean existsByQuadraAndDataAndHoraInicioAndHoraFim(
            Quadra quadra,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim
    );

    boolean existsByQuadraAndDataAndHoraInicioAndHoraFimAndIdNot(
            Quadra quadra,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim,
            Long id
    );
}
