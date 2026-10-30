package senac.agendasenac.dto;

import senac.agendasenac.entity.ReservaParticipante;

public record ReservaParticipanteDTO(

        ReservaResponseDTO reserva,
        ParticipanteDTO participante

) {

    public static ReservaParticipanteDTO fromEntity(
            ReservaParticipante reservaParticipante) {

        return new ReservaParticipanteDTO(
                ReservaResponseDTO.fromEntity(
                        reservaParticipante.getReserva()
                ),
                ParticipanteDTO.fromEntity(
                        reservaParticipante.getParticipante()
                )
        );
    }
}