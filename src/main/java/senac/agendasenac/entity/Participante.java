package senac.agendasenac.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "participante")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do participante é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O CPF do participante é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF do participante deve ter 11 números, sem pontos ou traços")
    @Column(nullable = false, unique = true)
    private String cpf;

    public Participante() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

}