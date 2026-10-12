
package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senac.agendasenac.entity.*;
import senac.agendasenac.repository.CheckinRepository;
import senac.agendasenac.repository.ReservaParticipanteRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class CheckinService {

    private static final ZoneId FUSO_HORARIO =
            ZoneId.of("America/Sao_Paulo");

    private final CheckinRepository checkinRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaParticipanteRepository reservaParticipanteRepository;

    public CheckinService(
            CheckinRepository checkinRepository,
            ReservaRepository reservaRepository,
            ReservaParticipanteRepository reservaParticipanteRepository) {

        this.checkinRepository = checkinRepository;
        this.reservaRepository = reservaRepository;
        this.reservaParticipanteRepository = reservaParticipanteRepository;
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

        LocalDateTime agora = LocalDateTime.now(FUSO_HORARIO);

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

        // 1. Registra o check-in.
        Checkin checkin = new Checkin();
        checkin.setReserva(reserva);
        checkin.setDataHora(agora);
        checkin.setStatus(StatusCheckin.REALIZADO);

        Checkin checkinSalvo = checkinRepository.saveAndFlush(checkin);

        // 2. Conclui a reserva.
        reserva.setStatus(StatusReserva.CONCLUIDA);
        reservaRepository.saveAndFlush(reserva);

        // 3. Remove os registros que dependem da reserva.
        checkinRepository.delete(checkinSalvo);

        reservaParticipanteRepository.deleteByReserva_Id(
                reserva.getId()
        );

        // 4. Exclui a reserva concluída.
        reservaRepository.delete(reserva);

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