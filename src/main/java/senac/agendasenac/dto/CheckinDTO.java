package senac.agendasenac.dto;

import senac.agendasenac.entity.Checkin;
import senac.agendasenac.entity.StatusCheckin;

import java.time.LocalDateTime;

public record CheckinDTO(
        Long id,
        ReservaResponseDTO reserva,
        LocalDateTime dataHora,
        StatusCheckin status
) {

    public static CheckinDTO fromEntity(Checkin checkin) {
        return new CheckinDTO(
                checkin.getId(),
                ReservaResponseDTO.fromEntity(checkin.getReserva()),
                checkin.getDataHora(),
                checkin.getStatus()
        );
    }
}