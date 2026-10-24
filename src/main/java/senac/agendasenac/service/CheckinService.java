
package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senac.agendasenac.entity.*;
import senac.agendasenac.repository.CheckinRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.time.LocalDateTime;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaService reservaService;
    private final ExclusaoReservaService exclusaoReservaService;
    private final Clock clock;

    public CheckinService(
            CheckinRepository checkinRepository,
            ReservaRepository reservaRepository,
            ReservaService reservaService,
            ExclusaoReservaService exclusaoReservaService,
            Clock clock) {

        this.checkinRepository = checkinRepository;
        this.reservaRepository = reservaRepository;
        this.reservaService = reservaService;
        this.exclusaoReservaService = exclusaoReservaService;
        this.clock = clock;
    }

    @Transactional
    public Checkin salvar(Long reservaId, String codigoAcesso) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() ->
                        new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException(
                    "Não é possível fazer check-in de uma reserva que não está ativa"
            );
        }

        if (!reserva.getCodigoAcesso().equals(codigoAcesso)) {
            throw new RuntimeException("Código de acesso inválido");
        }

        Horario horario = reserva.getHorario();

        LocalDateTime agora = LocalDateTime.now(clock);

        LocalDateTime inicioReserva = LocalDateTime.of(
                horario.getData(),
                horario.getHoraInicio()
        );

        LocalDateTime fimReserva = LocalDateTime.of(
                horario.getData(),
                horario.getHoraFim()
        );

        if (agora.isBefore(inicioReserva)
                || !agora.isBefore(fimReserva)) {

            throw new RuntimeException(
                    "Check-in permitido somente dentro do horário da reserva"
            );
        }

        if (checkinRepository.existsByReserva(reserva)) {
            throw new RuntimeException(
                    "A reserva já possui um check-in"
            );
        }

        // 1 Registra o check-in.
        Checkin checkin = new Checkin();
        checkin.setReserva(reserva);
        checkin.setDataHora(agora);
        checkin.setStatus(StatusCheckin.REALIZADO);

        Checkin checkinSalvo = checkinRepository.saveAndFlush(checkin);

        // 2 Conclui a reserva. A regra "só conclui com check-in realizado" fica em um lugar só: ReservaService.concluir.
        reservaService.concluir(reserva.getId());

        // 3 Apaga a reserva concluída e tudo que depende dela (check-in, vínculos e participantes que ficaram sem reserva).
        exclusaoReservaService.excluirReservaEDependencias(reserva);

        return checkinSalvo;
    }

    public List<Checkin> listarTodos() {
        return checkinRepository.findAll();
    }

    public Optional<Checkin> buscarPorId(Long id) {
        return checkinRepository.findById(id);
    }

    public Checkin atualizar(Long id, Checkin checkin) {

        Checkin checkinExistente = checkinRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Check-in não encontrado"));

        if (checkin.getReserva() == null
                || checkin.getReserva().getId() == null) {
            throw new RuntimeException("Reserva não informada");
        }

        Reserva reserva = reservaRepository.findById(
                checkin.getReserva().getId()
        ).orElseThrow(() ->
                new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException(
                    "Não é possível associar o check-in a uma reserva que não está ativa"
            );
        }

        if (!checkinExistente.getReserva().getId().equals(reserva.getId())
                && checkinRepository.existsByReserva(reserva)) {

            throw new RuntimeException(
                    "A reserva já possui um check-in"
            );
        }

        checkinExistente.setReserva(reserva);
        checkinExistente.setDataHora(checkin.getDataHora());

        return checkinRepository.save(checkinExistente);
    }

    public Checkin cancelar(Long id) {

        Checkin checkin = checkinRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Check-in não encontrado"));

        if (checkin.getStatus() != StatusCheckin.REALIZADO) {
            throw new RuntimeException(
                    "O check-in não está realizado"
            );
        }

        checkin.setStatus(StatusCheckin.CANCELADO);

        return checkinRepository.save(checkin);
    }

    public void excluir(Long id) {

        if (!checkinRepository.existsById(id)) {
            throw new RuntimeException("Check-in não encontrado");
        }

        checkinRepository.deleteById(id);
    }
}