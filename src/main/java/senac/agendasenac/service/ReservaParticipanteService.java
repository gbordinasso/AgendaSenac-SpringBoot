package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.ReservaParticipante;
import senac.agendasenac.entity.ReservaParticipanteId;
import senac.agendasenac.repository.ReservaParticipanteRepository;
import senac.agendasenac.exception.RecursoNaoEncontradoException;

import java.util.List;
import java.util.Optional;

// Aqui só se consulta e se remove o vínculo reserva <-> participante
// Para adicionar participante, a única porta de entrada é POST reservaService.adicionarParticipante
@Service
public class ReservaParticipanteService {

    private final ReservaParticipanteRepository reservaParticipanteRepository;

    public ReservaParticipanteService(ReservaParticipanteRepository reservaParticipanteRepository) {
        this.reservaParticipanteRepository = reservaParticipanteRepository;
    }

    public List<ReservaParticipante> listarTodos() {
        return reservaParticipanteRepository.findAll();
    }

    public Optional<ReservaParticipante> buscarPorId(ReservaParticipanteId id) {
        return reservaParticipanteRepository.findById(id);
    }

    public void excluir(ReservaParticipanteId id) {

        if (!reservaParticipanteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Participação não encontrada");
        }

        reservaParticipanteRepository.deleteById(id);
    }
}
