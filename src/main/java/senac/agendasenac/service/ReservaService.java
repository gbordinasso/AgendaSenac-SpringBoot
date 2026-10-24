package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.agendasenac.entity.*;
import senac.agendasenac.repository.*;

import java.security.SecureRandom;
import java.time.Clock;
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
    private final CheckinRepository checkinRepository;
    private final ExclusaoReservaService exclusaoReservaService;
    private final Clock clock;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            HorarioRepository horarioRepository,
            ReservaParticipanteRepository reservaParticipanteRepository,
            ParticipanteRepository participanteRepository,
            CheckinRepository checkinRepository,
            ExclusaoReservaService exclusaoReservaService,
            Clock clock) {

        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
        this.reservaParticipanteRepository = reservaParticipanteRepository;
        this.participanteRepository = participanteRepository;
        this.checkinRepository = checkinRepository;
        this.exclusaoReservaService = exclusaoReservaService;
        this.clock = clock;
    }

    @Transactional
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

        // Pode reservar a qualquer momento antes do início do horário,
        // sem prazo mínimo. Chegou a hora de início, não dá mais.
        LocalDateTime inicioHorario = horario.getData().atTime(horario.getHoraInicio());

        if (!LocalDateTime.now(clock).isBefore(inicioHorario)) {
            throw new RuntimeException("O horário já começou e não pode mais ser reservado");
        }

        if (reservaRepository.existsByHorarioAndStatus(horario, StatusReserva.ATIVA)) {
            throw new RuntimeException("Horário já reservado");
        }

        reserva.setHorario(horario);
        reserva.setStatus(StatusReserva.ATIVA);
        reserva.setDataReserva(LocalDateTime.now(clock));
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

    @Transactional
    public void cancelar(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("A reserva não está ativa");
        }

        Horario horario = reserva.getHorario();

        LocalDateTime inicioHorario = horario.getData().atTime(horario.getHoraInicio());

        LocalDateTime limiteCancelamento = inicioHorario.minusHours(2);

        if (LocalDateTime.now(clock).isAfter(limiteCancelamento)) {
            throw new RuntimeException(
                    "A reserva só pode ser cancelada com pelo menos 2 horas de antecedência. " +
                            "Em caso de necessidade, entre em contato com o suporte."
            );
        }

        horario.setStatus(StatusHorario.DISPONIVEL);

        horarioRepository.save(horario);

        exclusaoReservaService.excluirReservaEDependencias(reserva);
    }

    @Transactional
    public void concluir(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("A reserva não está ativa");
        }

        // A reserva só é concluída se existir um check-in realizado.
        if (!checkinRepository.existsByReserva_IdAndStatus(id, StatusCheckin.REALIZADO)) {
            throw new RuntimeException("A reserva só pode ser concluída depois do check-in");
        }

        reserva.setStatus(StatusReserva.CONCLUIDA);

        reservaRepository.save(reserva);
    }

    @Transactional
    public void excluir(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        // Se a reserva ainda estava ativa, devolve o horário para a lista
        if (reserva.getStatus() == StatusReserva.ATIVA) {
            Horario horario = reserva.getHorario();
            horario.setStatus(StatusHorario.DISPONIVEL);
            horarioRepository.save(horario);
        }

        exclusaoReservaService.excluirReservaEDependencias(reserva);
    }

    @Transactional
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