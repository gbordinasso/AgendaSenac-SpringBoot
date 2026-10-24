package senac.agendasenac.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.repository.CheckinRepository;
import senac.agendasenac.repository.ParticipanteRepository;
import senac.agendasenac.repository.ReservaParticipanteRepository;
import senac.agendasenac.repository.ReservaRepository;

import java.util.List;

// Único lugar do sistema que apaga uma reserva
// Cancelar, excluir, check-in e limpeza automática chamam este metodo
@Service
public class ExclusaoReservaService {

    private final ReservaRepository reservaRepository;
    private final CheckinRepository checkinRepository;
    private final ReservaParticipanteRepository reservaParticipanteRepository;
    private final ParticipanteRepository participanteRepository;

    public ExclusaoReservaService(
            ReservaRepository reservaRepository,
            CheckinRepository checkinRepository,
            ReservaParticipanteRepository reservaParticipanteRepository,
            ParticipanteRepository participanteRepository) {

        this.reservaRepository = reservaRepository;
        this.checkinRepository = checkinRepository;
        this.reservaParticipanteRepository = reservaParticipanteRepository;
        this.participanteRepository = participanteRepository;
    }

    @Transactional
    public void excluirReservaEDependencias(Reserva reserva) {

        Long reservaId = reserva.getId();

        // 1 Anota quem são os participantes desta reserva.
        //    Precisa vir antes de apagar os vínculos, senão não dá mais para saber.
        List<Long> participanteIds = reservaParticipanteRepository.findByReserva_Id(reservaId)
                .stream()
                .map(vinculo -> vinculo.getParticipante().getId())
                .toList();

        // 2 Apaga o que aponta para a reserva.
        checkinRepository.deleteByReserva_Id(reservaId);
        reservaParticipanteRepository.deleteByReserva_Id(reservaId);

        // 3 Apaga a reserva.
        reservaRepository.delete(reserva);
        reservaRepository.flush();

        // 4 Apaga os participantes anotados que não estão em nenhuma outra reserva.
        //    Quem ainda está em outra reserva continua no banco.
        for (Long participanteId : participanteIds) {
            if (!reservaParticipanteRepository.existsByParticipante_Id(participanteId)) {
                participanteRepository.deleteById(participanteId);
            }
        }
    }
}
