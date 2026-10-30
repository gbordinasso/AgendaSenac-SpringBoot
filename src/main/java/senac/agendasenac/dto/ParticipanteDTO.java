package senac.agendasenac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import senac.agendasenac.entity.Participante;

public record ParticipanteDTO(
        Long id,
        @NotBlank(message = "O nome do participante é obrigatório")
        String nome,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "O CPF do participante é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 números, sem pontos ou traços")
        String cpf

) {

    public Participante toEntity() {
        Participante participante = new Participante();
        participante.setNome(nome);
        participante.setCpf(cpf);
        return participante;
    }

    public static ParticipanteDTO fromEntity(Participante participante) {
        return new ParticipanteDTO(participante.getId(), participante.getNome(), null);
    }
}