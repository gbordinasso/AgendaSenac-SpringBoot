package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Quadra;
import senac.agendasenac.entity.StatusHorario;
import senac.agendasenac.entity.StatusQuadra;
import senac.agendasenac.repository.HorarioRepository;
import senac.agendasenac.repository.QuadraRepository;
import senac.agendasenac.repository.ReservaRepository;
import senac.agendasenac.exception.ConflitoException;
import senac.agendasenac.exception.RecursoNaoEncontradoException;

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
            throw new ConflitoException("Quadra já cadastrada");
        }

        return quadraRepository.save(quadra);
    }

    public List<Quadra> listarTodos() {
        return quadraRepository.findAll();
    }

    public Optional<Quadra> buscarPorId(Long id) {
        return quadraRepository.findById(id);
    }

    @Transactional
    public Quadra atualizar(Long id, Quadra quadra) {

        Quadra quadraExistente = quadraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quadra não encontrada"));

        if (!quadraExistente.getNome().equals(quadra.getNome())
                && quadraRepository.existsByNome(quadra.getNome())) {
            throw new ConflitoException("Quadra já cadastrada");
        }

        StatusQuadra statusAnterior = quadraExistente.getStatus();

        quadraExistente.setNome(quadra.getNome());
        quadraExistente.setLocalizacao(quadra.getLocalizacao());
        quadraExistente.setModalidade(quadra.getModalidade());
        quadraExistente.setFoto(quadra.getFoto());
        quadraExistente.setStatus(quadra.getStatus());

        Quadra quadraSalva = quadraRepository.save(quadraExistente);

        sincronizarHorarios(quadraSalva, statusAnterior);

        return quadraSalva;
    }

    // Se a quadra entrar em manutação os horários deixam de ser permitidos
    // Caso a quadra volta, os horários voltam a ficar permitidos
    // Reservas que já existem não são mexidas
    private void sincronizarHorarios(Quadra quadra, StatusQuadra statusAnterior) {

        if (quadra.getStatus() == statusAnterior) {
            return;
        }

        StatusHorario de;
        StatusHorario para;

        if (quadra.getStatus() == StatusQuadra.INDISPONIVEL) {
            de = StatusHorario.DISPONIVEL;
            para = StatusHorario.INDISPONIVEL;
        } else if (quadra.getStatus() == StatusQuadra.DISPONIVEL) {
            de = StatusHorario.INDISPONIVEL;
            para = StatusHorario.DISPONIVEL;
        } else {
            return;
        }

        List<Horario> horarios = horarioRepository.findByQuadra_IdAndStatus(quadra.getId(), de);

        for (Horario horario : horarios) {
            horario.setStatus(para);
        }

        horarioRepository.saveAll(horarios);
    }

    @Transactional
    public void excluir(Long id) {

        if (!quadraRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Quadra não encontrada");
        }

        if (reservaRepository.existsByHorario_Quadra_Id(id)) {
            throw new ConflitoException("A quadra possui reservas e não pode ser excluída");
        }

        // Se não tiver reservas, apaga os horários da quadra e depois a própria quadra.
        horarioRepository.deleteByQuadra_Id(id);
        quadraRepository.deleteById(id);
    }
}