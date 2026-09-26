package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Participante;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.ReservaParticipante;
import senac.agendasenac.entity.ReservaParticipanteId;
import senac.agendasenac.entity.StatusReserva;
import senac.agendasenac.repository.ParticipanteRepository;
import senac.agendasenac.repository.ReservaParticipanteRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaParticipanteService {

    private final ReservaParticipanteRepository reservaParticipanteRepository;
    private final ReservaRepository reservaRepository;
    private final ParticipanteRepository participanteRepository;

    public ReservaParticipanteService(
            ReservaParticipanteRepository reservaParticipanteRepository,
            ReservaRepository reservaRepository,
            ParticipanteRepository participanteRepository) {

        this.reservaParticipanteRepository = reservaParticipanteRepository;
        this.reservaRepository = reservaRepository;
        this.participanteRepository = participanteRepository;
    }

    public ReservaParticipante salvar(ReservaParticipante reservaParticipante) {

        if (!reservaRepository.existsById(
                reservaParticipante.getReserva().getId())) {

            throw new RuntimeException("Reserva não encontrada");
        }

        if (!participanteRepository.existsById(
                reservaParticipante.getParticipante().getId())) {

            throw new RuntimeException("Participante não encontrado");
        }

        Reserva reserva = reservaRepository.findById(reservaParticipante.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("Não é possível adicionar participante a uma reserva que não está ativa");
        }

        if (reservaParticipanteRepository.existsById(reservaParticipante.getId())) {
            throw new RuntimeException("Participante já está vinculado a esta reserva");
        }

        Participante participante = participanteRepository.findById(reservaParticipante.getParticipante().getId())
                .orElseThrow(() -> new RuntimeException("Participante não encontrado"));

        reservaParticipante.setReserva(reserva);
        reservaParticipante.setParticipante(participante);

        return reservaParticipanteRepository.save(reservaParticipante);
    }

    public List<ReservaParticipante> listarTodos() {
        return reservaParticipanteRepository.findAll();
    }

    public Optional<ReservaParticipante> buscarPorId(ReservaParticipanteId id) {
        return reservaParticipanteRepository.findById(id);
    }

    public ReservaParticipante atualizar(ReservaParticipanteId id, ReservaParticipante reservaParticipante) {

        ReservaParticipante reservaParticipanteExistente = reservaParticipanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participação não encontrada"));

        if (!reservaRepository.existsById(reservaParticipante.getReserva().getId())) {
            throw new RuntimeException("Reserva não encontrada");
        }

        if (!participanteRepository.existsById(reservaParticipante.getParticipante().getId())) {
            throw new RuntimeException("Participante não encontrado");
        }

        Reserva reserva = reservaRepository.findById(reservaParticipante.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("Não é possível adicionar participante a uma reserva que não está ativa");
        }

        ReservaParticipanteId novoId = reservaParticipante.getId();

        if (!id.equals(novoId) && reservaParticipanteRepository.existsById(novoId)) {
            throw new RuntimeException("Participante já está vinculado a esta reserva");
        }

        Participante participante = participanteRepository.findById(reservaParticipante.getParticipante().getId())
                .orElseThrow(() -> new RuntimeException("Participante não encontrado"));

        reservaParticipanteExistente.setReserva(reserva);
        reservaParticipanteExistente.setParticipante(participante);

        return reservaParticipanteRepository.save(reservaParticipanteExistente);
    }

    public void excluir(ReservaParticipanteId id) {

        if (!reservaParticipanteRepository.existsById(id)) {
            throw new RuntimeException("Participação não encontrada");
        }

        reservaParticipanteRepository.deleteById(id);
    }
}
