package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.StatusCheckin;
import senac.agendasenac.entity.StatusReserva;
import senac.agendasenac.repository.CheckinRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final ReservaRepository reservaRepository;

    public CheckinService(
            CheckinRepository checkinRepository,
            ReservaRepository reservaRepository) {

        this.checkinRepository = checkinRepository;
        this.reservaRepository = reservaRepository;
    }

    public Checkin salvar(Checkin checkin) {

        if (!reservaRepository.existsById(checkin.getReserva().getId())) {
            throw new RuntimeException("Reserva não encontrada");
        }

        Reserva reserva = reservaRepository.findById(checkin.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("Não é possível fazer check-in de uma reserva que não está ativa");
        }

        if (checkinRepository.existsByReserva(reserva)) {
            throw new RuntimeException("A reserva já possui um check-in");
        }

        checkin.setReserva(reserva);
        checkin.setStatus(StatusCheckin.REALIZADO);

        return checkinRepository.save(checkin);
    }

    public List<Checkin> listarTodos() {
        return checkinRepository.findAll();
    }

    public Optional<Checkin> buscarPorId(Long id) {
        return checkinRepository.findById(id);
    }

    public Checkin atualizar(Long id, Checkin checkin) {

        Checkin checkinExistente = checkinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Check-in não encontrado"));

        if (!reservaRepository.existsById(checkin.getReserva().getId())) {
            throw new RuntimeException("Reserva não encontrada");
        }

        Reserva reserva = reservaRepository.findById(checkin.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("Não é possível associar o check-in a uma reserva que não está ativa");
        }

        if (!checkinExistente.getReserva().getId().equals(reserva.getId())
                && checkinRepository.existsByReserva(reserva)) {

            throw new RuntimeException("A reserva já possui um check-in");
        }

        checkinExistente.setReserva(reserva);
        checkinExistente.setDataHora(checkin.getDataHora());

        return checkinRepository.save(checkinExistente);
    }

    public Checkin cancelar(Long id) {

        Checkin checkin = checkinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Check-in não encontrado"));

        if (checkin.getStatus() != StatusCheckin.REALIZADO) {
            throw new RuntimeException("O check-in não está realizado");
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
