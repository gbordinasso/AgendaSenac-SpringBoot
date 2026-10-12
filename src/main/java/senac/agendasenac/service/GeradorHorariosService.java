
package senac.agendasenac.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Quadra;
import senac.agendasenac.entity.StatusHorario;
import senac.agendasenac.entity.StatusQuadra;
import senac.agendasenac.repository.HorarioRepository;
import senac.agendasenac.repository.QuadraRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class GeradorHorariosService implements CommandLineRunner {

    private static final ZoneId FUSO_HORARIO =
            ZoneId.of("America/Sao_Paulo");

    private final HorarioRepository horarioRepository;
    private final QuadraRepository quadraRepository;

    public GeradorHorariosService(
            HorarioRepository horarioRepository,
            QuadraRepository quadraRepository) {
        this.horarioRepository = horarioRepository;
        this.quadraRepository = quadraRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        LocalDate hoje = LocalDate.now(FUSO_HORARIO);
        LocalDate limite = hoje.plusDays(14);
        LocalTime agora = LocalTime.now(FUSO_HORARIO);

        List<Quadra> quadras = quadraRepository.findAll();

        for (Quadra quadra : quadras) {
            if (quadra.getStatus() != StatusQuadra.DISPONIVEL) {
                continue;
            }

            for (LocalDate data = hoje;
                 !data.isAfter(limite);
                 data = data.plusDays(1)) {

                DayOfWeek dia = data.getDayOfWeek();

                if (dia == DayOfWeek.SUNDAY) {
                    continue;
                }

                for (int hora = 8; hora < 22; hora += 2) {
                    LocalTime inicio = LocalTime.of(hora, 0);
                    LocalTime fim = inicio.plusHours(2);

                    if (dia == DayOfWeek.SATURDAY
                            && fim.isAfter(LocalTime.of(16, 0))) {
                        continue;
                    }

                    if (data.equals(hoje)
                            && !inicio.isAfter(agora)) {
                        continue;
                    }

                    boolean existe =
                            horarioRepository
                                    .existsByQuadraIdAndDataAndHoraInicioAndHoraFim(
                                            quadra.getId(),
                                            data,
                                            inicio,
                                            fim
                                    );

                    if (existe) {
                        continue;
                    }

                    Horario horario = new Horario();
                    horario.setQuadra(quadra);
                    horario.setData(data);
                    horario.setHoraInicio(inicio);
                    horario.setHoraFim(fim);
                    horario.setStatus(StatusHorario.DISPONIVEL);

                    horarioRepository.save(horario);
                }
            }
        }
    }
}
