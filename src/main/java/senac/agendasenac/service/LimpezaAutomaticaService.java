
package senac.agendasenac.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.StatusCheckin;
import senac.agendasenac.entity.StatusReserva;
import senac.agendasenac.repository.CheckinRepository;
import senac.agendasenac.repository.HorarioRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.time.LocalDateTime;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
public class LimpezaAutomaticaService {

    private final HorarioRepository horarioRepository;
    private final ReservaRepository reservaRepository;
    private final CheckinRepository checkinRepository;
    private final ExclusaoReservaService exclusaoReservaService;
    private final Clock clock;

    public LimpezaAutomaticaService(
            HorarioRepository horarioRepository,
            ReservaRepository reservaRepository,
            CheckinRepository checkinRepository,
            ExclusaoReservaService exclusaoReservaService,
            Clock clock) {

        this.horarioRepository = horarioRepository;
        this.reservaRepository = reservaRepository;
        this.checkinRepository = checkinRepository;
        this.exclusaoReservaService = exclusaoReservaService;
        this.clock = clock;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void limparRegistrosEncerrados() {

        LocalDateTime agora = LocalDateTime.now(clock);

        // 1. Exclui reservas que já não estão ativas.
        List<Reserva> reservas = reservaRepository.findAll();

        for (Reserva reserva : reservas) {
            if (reserva.getStatus() != StatusReserva.ATIVA) {
                exclusaoReservaService.excluirReservaEDependencias(reserva);
            }
        }

        // 2. Procura horários que já chegou ao fim.
        List<Horario> horarios = horarioRepository.findAll();

        for (Horario horario : horarios) {

            LocalDateTime fimHorario = LocalDateTime.of(
                    horario.getData(),
                    horario.getHoraFim()
            );

            if (agora.isBefore(fimHorario)) {
                continue;
            }

            Optional<Reserva> reservaOptional =
                    reservaRepository.findByHorario(horario);

            if (reservaOptional.isPresent()) {

                Reserva reserva = reservaOptional.get();

                if (reserva.getStatus() == StatusReserva.ATIVA) {

                    Optional<Checkin> checkinOptional =
                            checkinRepository.findByReserva(reserva);

                    boolean checkinRealizado =
                            checkinOptional.isPresent()
                                    && checkinOptional.get().getStatus()
                                    == StatusCheckin.REALIZADO;

                    if (checkinRealizado) {
                        reserva.setStatus(StatusReserva.CONCLUIDA);
                    } else {
                        reserva.setStatus(StatusReserva.NAO_COMPARECEU);
                    }

                    reservaRepository.save(reserva);
                }

                // Remove a reserva e tudo que depende dela.
                exclusaoReservaService.excluirReservaEDependencias(reserva);
            }

            // Sem reserva vinculada, ou após removê-la,
            // o horário encerrado pode ser excluído.
            horarioRepository.delete(horario);
        }
    }
}