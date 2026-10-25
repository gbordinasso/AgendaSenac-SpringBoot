package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Quadra;
import senac.agendasenac.entity.StatusHorario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    void deleteByQuadra_Id(Long quadraId);

    List<Horario> findByQuadra_IdAndStatus(Long quadraId, StatusHorario status);

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