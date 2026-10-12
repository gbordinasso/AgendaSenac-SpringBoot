package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.*;
import senac.agendasenac.repository.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;
    private final ReservaParticipanteRepository reservaParticipanteRepository;
    private final ParticipanteRepository participanteRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            HorarioRepository horarioRepository,
            ReservaParticipanteRepository reservaParticipanteRepository,
            ParticipanteRepository participanteRepository) {

        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
        this.reservaParticipanteRepository = reservaParticipanteRepository;
        this.participanteRepository = participanteRepository;
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

        if (reservaRepository.existsByHorarioAndStatus(horario, StatusReserva.ATIVA)) {
            throw new RuntimeException("Horário já reservado");
        }

        reserva.setHorario(horario);
        reserva.setStatus(StatusReserva.ATIVA);
        reserva.setDataReserva(LocalDateTime.now());
        reserva.setCodigoAcesso(gerarCodigoAcessoUnico());

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

    public void cancelar(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("A reserva não está ativa");
        }

        Horario horario = reserva.getHorario();

        LocalDateTime inicioHorario = horario.getData().atTime(horario.getHoraInicio());

        LocalDateTime limiteCancelamento = inicioHorario.minusHours(2);

        if (LocalDateTime.now().isAfter(limiteCancelamento)) {
            throw new RuntimeException(
                    "A reserva só pode ser cancelada com pelo menos 2 horas de antecedência. " +
                            "Em caso de necessidade, entre em contato com o suporte."
            );
        }

        horario.setStatus(StatusHorario.DISPONIVEL);

        horarioRepository.save(horario);

        reservaRepository.delete(reserva);
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

    public void adicionarParticipante(Long reservaId, String nome, String cpf) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("A reserva não está ativa");
        }

        Optional<Participante> participanteExistente = participanteRepository.findByCpf(cpf);

        Participante participante;

        if (participanteExistente.isEmpty()) {
            Participante novoParticipante = new Participante();

            novoParticipante.setNome(nome);
            novoParticipante.setCpf(cpf);

            participante = participanteRepository.save(novoParticipante);
        } else {
            participante = participanteExistente.get();
        }

        ReservaParticipanteId id = new ReservaParticipanteId(reservaId, participante.getId());

        if (reservaParticipanteRepository.existsById(id)) {
            throw new RuntimeException("Participante já está adicionado à reserva");
        }

        ReservaParticipante reservaParticipante = new ReservaParticipante();

        reservaParticipante.setId(id);
        reservaParticipante.setReserva(reserva);
        reservaParticipante.setParticipante(participante);

        reservaParticipanteRepository.save(reservaParticipante);
    }

    private String gerarCodigoAcesso() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            int indice = secureRandom.nextInt(caracteres.length());

            codigo.append(caracteres.charAt(indice));
        }

        codigo.insert(4, '-');

        return codigo.toString();
    }

    private String gerarCodigoAcessoUnico() {
        String codigo;

        do {
            codigo = gerarCodigoAcesso();
        } while (reservaRepository.existsByCodigoAcesso(codigo));

        return codigo;
    }
}
