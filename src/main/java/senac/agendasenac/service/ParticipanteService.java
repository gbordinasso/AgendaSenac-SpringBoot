package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Participante;
import senac.agendasenac.repository.ParticipanteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
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
        participanteExistente.setCurso(participante.getCurso());

        return participanteRepository.save(participanteExistente);
    }

    public void excluir(Long id) {

        if (!participanteRepository.existsById(id)) {
            throw new RuntimeException("Participante não encontrado");
        }

        participanteRepository.deleteById(id);
    }
}