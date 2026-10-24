package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.agendasenac.entity.Participante;
import senac.agendasenac.repository.ParticipanteRepository;
import senac.agendasenac.repository.ReservaParticipanteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final ReservaParticipanteRepository reservaParticipanteRepository;

    public ParticipanteService(
            ParticipanteRepository participanteRepository,
            ReservaParticipanteRepository reservaParticipanteRepository) {
        this.participanteRepository = participanteRepository;
        this.reservaParticipanteRepository = reservaParticipanteRepository;
    }

    public Participante salvar(Participante participante) {

        if (participanteRepository.existsByCpf(participante.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        return participanteRepository.save(participante);
    }

    public List<Participante> listarTodos() {
        return participanteRepository.findAll();
    }

    public Optional<Participante> buscarPorId(Long id) {
        return participanteRepository.findById(id);
    }

    public Participante atualizar(Long id, Participante participante) {

        Participante participanteExistente = participanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante não encontrado"));

        if (!participanteExistente.getCpf().equals(participante.getCpf())
                && participanteRepository.existsByCpf(participante.getCpf())) {

            throw new RuntimeException("CPF já cadastrado");
        }

        participanteExistente.setNome(participante.getNome());
        participanteExistente.setCpf(participante.getCpf());

        return participanteRepository.save(participanteExistente);
    }

    @Transactional
    public void excluir(Long id) {

        if (!participanteRepository.existsById(id)) {
            throw new RuntimeException("Participante não encontrado");
        }

        // Remove primeiro os vínculos com reservas, depois o participante
        reservaParticipanteRepository.deleteByParticipante_Id(id);
        participanteRepository.deleteById(id);
    }
}