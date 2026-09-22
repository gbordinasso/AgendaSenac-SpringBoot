package senac.agendasenac.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva_participante")
public class ReservaParticipante {

    @EmbeddedId
    private ReservaParticipanteId id;

    @ManyToOne
    @MapsId("reservaId")
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne
    @MapsId("participanteId")
    @JoinColumn(name = "id_participante", nullable = false)
    private Participante participante;


    public ReservaParticipante() {

    }

    public ReservaParticipanteId getId() {
        return id;
    }

    public void setId(ReservaParticipanteId id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }
}