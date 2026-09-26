package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Quadra;
import senac.agendasenac.repository.QuadraRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuadraService {

    private final QuadraRepository quadraRepository;

    public QuadraService(QuadraRepository quadraRepository) {
        this.quadraRepository = quadraRepository;
    }

    public Quadra salvar(Quadra quadra) {

        if (quadraRepository.existsByNome(quadra.getNome())) {
            throw new RuntimeException("Quadra já cadastrada");
        }

        return quadraRepository.save(quadra);
    }

    public List<Quadra> listarTodos() {
        return quadraRepository.findAll();
    }

    public Optional<Quadra> buscarPorId(Long id) {
        return quadraRepository.findById(id);
    }

    public Quadra atualizar(Long id, Quadra quadra) {

        Quadra quadraExistente = quadraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quadra não encontrada"));

        if (!quadraExistente.getNome().equals(quadra.getNome())
                && quadraRepository.existsByNome(quadra.getNome())) {
            throw new RuntimeException("Quadra já cadastrada");
        }

        quadraExistente.setNome(quadra.getNome());
        quadraExistente.setLocalizacao(quadra.getLocalizacao());
        quadraExistente.setModalidade(quadra.getModalidade());
        quadraExistente.setStatus(quadra.getStatus());

        return quadraRepository.save(quadraExistente);
    }

    public void excluir(Long id) {

        if (!quadraRepository.existsById(id)) {
            throw new RuntimeException("Quadra não encontrada");
        }

        quadraRepository.deleteById(id);
    }
}