package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.StatusHorario;
import senac.agendasenac.entity.StatusReserva;
import senac.agendasenac.repository.HorarioRepository;
import senac.agendasenac.repository.ReservaRepository;
import senac.agendasenac.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            HorarioRepository horarioRepository) {

        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
    }

    public Reserva salvar(Reserva reserva) {

        if (!usuarioRepository.existsById(reserva.getUsuario().getId())) {
            throw new RuntimeException("Usuário não encontrado");
        }

        if (!horarioRepository.existsById(reserva.getHorario().getId())) {
            throw new RuntimeException("Horário não encontrado");
        }

        Horario horario = horarioRepository.findById(reserva.getHorario().getId())
                .orElseThrow(() -> new RuntimeException("Horário não encontrado"));

        if (horario.getStatus() != StatusHorario.DISPONIVEL) {
            throw new RuntimeException("Horário não está disponível");
        }

        if (reservaRepository.existsByHorario(horario)) {
            throw new RuntimeException("Horário já reservado");
        }

        reserva.setHorario(horario);
        reserva.setStatus(StatusReserva.ATIVA);

        Reserva reservaSalva = reservaRepository.save(reserva);

        horario.setStatus(StatusHorario.RESERVADO);
        horarioRepository.save(horario);

        return reservaSalva;
    }

    public List<Reserva> listarTodos() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva atualizar(Long id, Reserva reserva) {

        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (!usuarioRepository.existsById(reserva.getUsuario().getId())) {
            throw new RuntimeException("Usuário não encontrado");
        }

        reservaExistente.setUsuario(reserva.getUsuario());
        reservaExistente.setCodigoAcesso(reserva.getCodigoAcesso());
        reservaExistente.setStatus(reserva.getStatus());
        reservaExistente.setDataReserva(reserva.getDataReserva());

        return reservaRepository.save(reservaExistente);
    }

    public Reserva cancelar(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("A reserva não está ativa");
        }

        reserva.setStatus(StatusReserva.CANCELADA);

        Horario horario = reserva.getHorario();
        horario.setStatus(StatusHorario.DISPONIVEL);

        horarioRepository.save(horario);

        return reservaRepository.save(reserva);
    }

    public Reserva concluir(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("A reserva não está ativa");
        }

        reserva.setStatus(StatusReserva.CONCLUIDA);

        return reservaRepository.save(reserva);
    }

    public void excluir(Long id) {

        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva não encontrada");
        }

        reservaRepository.deleteById(id);
    }
}
