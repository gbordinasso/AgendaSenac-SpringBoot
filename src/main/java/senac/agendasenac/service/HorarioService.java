
package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.StatusReserva;
import senac.agendasenac.repository.HorarioRepository;
import senac.agendasenac.repository.ReservaRepository;
import senac.agendasenac.exception.ConflitoException;
import senac.agendasenac.exception.RecursoNaoEncontradoException;
import senac.agendasenac.exception.RequisicaoInvalidaException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final ReservaRepository reservaRepository;
    private final Clock clock;

    public HorarioService(
            HorarioRepository horarioRepository,
            ReservaRepository reservaRepository,
            Clock clock) {

        this.horarioRepository = horarioRepository;
        this.reservaRepository = reservaRepository;
        this.clock = clock;
    }

    public Horario salvar(Horario horario) {

        if (horario.getQuadra() == null || horario.getQuadra().getId() == null) {
            throw new RequisicaoInvalidaException("A quadra é obrigatória");
        }

        if (!horario.getHoraInicio().isBefore(horario.getHoraFim())) {
            throw new RequisicaoInvalidaException(
                    "Horário de início deve ser anterior ao horário de fim"
            );
        }

        if (!horarioValido(
                horario.getHoraInicio(),
                horario.getHoraFim())) {

            throw new RequisicaoInvalidaException(
                    "Horário deve seguir um dos blocos permitidos de 2 horas"
            );
        }

        validarDataEFuncionamento(
                horario.getData(),
                horario.getHoraInicio(),
                horario.getHoraFim()
        );

        if (horarioRepository
                .existsByQuadraAndDataAndHoraInicioLessThanAndHoraFimGreaterThan(
                        horario.getQuadra(),
                        horario.getData(),
                        horario.getHoraFim(),
                        horario.getHoraInicio())) {

            throw new ConflitoException(
                    "Horário já cadastrado ou em conflito para esta quadra"
            );
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
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Horário não encontrado"));

        if (!horario.getHoraInicio().isBefore(horario.getHoraFim())) {
            throw new RequisicaoInvalidaException(
                    "Horário de início deve ser anterior ao horário de fim"
            );
        }

        if (!horarioValido(
                horario.getHoraInicio(),
                horario.getHoraFim())) {

            throw new RequisicaoInvalidaException(
                    "Horário deve seguir um dos blocos permitidos de 2 horas"
            );
        }

        validarDataEFuncionamento(
                horario.getData(),
                horario.getHoraInicio(),
                horario.getHoraFim()
        );

        if (horarioRepository
                .existsByQuadraAndDataAndHoraInicioLessThanAndHoraFimGreaterThanAndIdNot(
                        horarioExistente.getQuadra(),
                        horario.getData(),
                        horario.getHoraFim(),
                        horario.getHoraInicio(),
                        id)) {

            throw new ConflitoException(
                    "Horário já cadastrado ou em conflito para esta quadra"
            );
        }

        horarioExistente.setData(horario.getData());
        horarioExistente.setHoraInicio(horario.getHoraInicio());
        horarioExistente.setHoraFim(horario.getHoraFim());
        horarioExistente.setStatus(horario.getStatus());

        return horarioRepository.save(horarioExistente);
    }

    public void excluir(Long id) {

        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Horário não encontrado"));

        if (reservaRepository.existsByHorarioAndStatus(
                horario, StatusReserva.ATIVA)) {

            throw new ConflitoException(
                    "Não é possível excluir um horário que possui uma reserva"
            );
        }

        horarioRepository.deleteById(id);
    }

    private void validarDataEFuncionamento(
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim) {

        if (data == null || horaInicio == null || horaFim == null) {
            throw new RequisicaoInvalidaException(
                    "Data, horário de início e horário de fim são obrigatórios"
            );
        }

        LocalDate hoje = LocalDate.now(clock);

        if (data.isBefore(hoje)) {
            throw new RequisicaoInvalidaException(
                    "Não é permitido cadastrar horários em datas passadas"
            );
        }

        if (data.isAfter(hoje.plusDays(14))) {
            throw new RequisicaoInvalidaException(
                    "Só é permitido cadastrar horários com até 14 dias de antecedência"
            );
        }

        if (data.equals(hoje)
                && !horaInicio.isAfter(LocalTime.now(clock))) {

            throw new RequisicaoInvalidaException(
                    "Não é permitido cadastrar um horário que já começou ou passou"
            );
        }

        DayOfWeek diaSemana = data.getDayOfWeek();

        if (diaSemana == DayOfWeek.SUNDAY) {
            throw new RequisicaoInvalidaException(
                    "Não é permitido cadastrar horários aos domingos"
            );
        }

        if (diaSemana == DayOfWeek.SATURDAY
                && horaFim.isAfter(LocalTime.of(16, 0))) {

            throw new RequisicaoInvalidaException(
                    "Aos sábados, o funcionamento termina às 16:00"
            );
        }
    }

    private boolean horarioValido(
            LocalTime horaInicio,
            LocalTime horaFim) {

        return (horaInicio.equals(LocalTime.of(8, 0))
                && horaFim.equals(LocalTime.of(10, 0)))
                || (horaInicio.equals(LocalTime.of(10, 0))
                && horaFim.equals(LocalTime.of(12, 0)))
                || (horaInicio.equals(LocalTime.of(12, 0))
                && horaFim.equals(LocalTime.of(14, 0)))
                || (horaInicio.equals(LocalTime.of(14, 0))
                && horaFim.equals(LocalTime.of(16, 0)))
                || (horaInicio.equals(LocalTime.of(16, 0))
                && horaFim.equals(LocalTime.of(18, 0)))
                || (horaInicio.equals(LocalTime.of(18, 0))
                && horaFim.equals(LocalTime.of(20, 0)))
                || (horaInicio.equals(LocalTime.of(20, 0))
                && horaFim.equals(LocalTime.of(22, 0)));
    }
}