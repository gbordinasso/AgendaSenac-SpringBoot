package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.repository.HorarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public Horario salvar(Horario horario) {

        if (horarioRepository.existsByQuadraAndDataAndHoraInicioAndHoraFim(
                horario.getQuadra(),
                horario.getData(),
                horario.getHoraInicio(),
                horario.getHoraFim())) {

            throw new RuntimeException("Horário já cadastrado para esta quadra");
        }

        return horarioRepository.save(horario);
    }

    public List<Horario> listarTodos() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> buscarPorId(Long id) {
        return horarioRepository.findById(id);
    }

    public Horario atualizar(Long id, Horario horario) {

        Horario horarioExistente = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horário não encontrado"));

        if (horarioRepository.existsByQuadraAndDataAndHoraInicioAndHoraFimAndIdNot(
                horario.getQuadra(),
                horario.getData(),
                horario.getHoraInicio(),
                horario.getHoraFim(),
                id)) {

            throw new RuntimeException("Horário já cadastrado para esta quadra");
        }

        horarioExistente.setQuadra(horario.getQuadra());
        horarioExistente.setData(horario.getData());
        horarioExistente.setHoraInicio(horario.getHoraInicio());
        horarioExistente.setHoraFim(horario.getHoraFim());
        horarioExistente.setStatus(horario.getStatus());

        return horarioRepository.save(horarioExistente);
    }

    public void excluir(Long id) {

        if (!horarioRepository.existsById(id)) {
            throw new RuntimeException("Horário não encontrado");
        }

        horarioRepository.deleteById(id);
    }
}