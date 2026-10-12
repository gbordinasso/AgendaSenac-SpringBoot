package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Quadra;

import java.time.LocalDate;
import java.time.LocalTime;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    boolean existsByQuadraAndDataAndHoraInicioLessThanAndHoraFimGreaterThan(
            Quadra quadra,
            LocalDate data,
            LocalTime horaFim,
            LocalTime horaInicio
    );

    boolean existsByQuadraAndDataAndHoraInicioLessThanAndHoraFimGreaterThanAndIdNot(
            Quadra quadra,
            LocalDate data,
            LocalTime horaFim,
            LocalTime horaInicio,
            Long id
    );


    boolean existsByQuadraIdAndDataAndHoraInicioAndHoraFim(
            Long quadraId,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim
    );

}