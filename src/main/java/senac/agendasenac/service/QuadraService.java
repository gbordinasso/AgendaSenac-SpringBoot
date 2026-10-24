package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.agendasenac.entity.Quadra;
import senac.agendasenac.repository.HorarioRepository;
import senac.agendasenac.repository.QuadraRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuadraService {

    private final QuadraRepository quadraRepository;
    private final HorarioRepository horarioRepository;
    private final ReservaRepository reservaRepository;

    public QuadraService(
            QuadraRepository quadraRepository,
            HorarioRepository horarioRepository,
            ReservaRepository reservaRepository) {
        this.quadraRepository = quadraRepository;
        this.horarioRepository = horarioRepository;
        this.reservaRepository = reservaRepository;
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
        quadraExistente.setFoto(quadra.getFoto());
        quadraExistente.setStatus(quadra.getStatus());

        return quadraRepository.save(quadraExistente);
    }

    @Transactional
    public void excluir(Long id) {

        if (!quadraRepository.existsById(id)) {
            throw new RuntimeException("Quadra não encontrada");
        }

        if (reservaRepository.existsByHorario_Quadra_Id(id)) {
            throw new RuntimeException("A quadra possui reservas e não pode ser excluída");
        }

        // Sem reservas: apaga os horários da quadra e depois a própria quadra.
        horarioRepository.deleteByQuadra_Id(id);
        quadraRepository.deleteById(id);
    }
}