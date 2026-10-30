package senac.agendasenac.dto;

import senac.agendasenac.entity.Horario;
import senac.agendasenac.entity.Reserva;
import senac.agendasenac.entity.StatusReserva;

import java.time.LocalDateTime;

public record ReservaResponseDTO(

        Long id,
        UsuarioDTO usuario,
        Horario horario,
        String codigoAcesso,
        StatusReserva status,
        LocalDateTime dataReserva

) {

    public static ReservaResponseDTO fromEntity(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                UsuarioDTO.fromEntity(reserva.getUsuario()),
                reserva.getHorario(),
                reserva.getCodigoAcesso(),
                reserva.getStatus(),
                reserva.getDataReserva()
        );
    }
}