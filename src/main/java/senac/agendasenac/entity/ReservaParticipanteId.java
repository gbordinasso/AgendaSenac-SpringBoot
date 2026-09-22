package senac.agendasenac.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReservaParticipanteId implements Serializable {

    private Long reservaId;
    private Long participanteId;

    public ReservaParticipanteId() {

    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReservaParticipanteId that = (ReservaParticipanteId) o;
        return Objects.equals(reservaId, that.reservaId) && Objects.equals(participanteId, that.participanteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservaId, participanteId);
    }
}