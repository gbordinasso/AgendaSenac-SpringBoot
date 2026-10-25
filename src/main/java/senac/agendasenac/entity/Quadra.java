package senac.agendasenac.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "quadra")
public class Quadra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da quadra é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "A localização da quadra é obrigatória")
    @Column(nullable = false)
    private String localizacao;

    @NotBlank(message = "A modalidade da quadra é obrigatória")
    @Column(nullable = false)
    private String modalidade;

    @NotBlank(message = "A foto da quadra é obrigatória")
    @Column(nullable = false)
    private String foto;

    @NotNull(message = "O status da quadra é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusQuadra status;


    public Quadra() {

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

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public StatusQuadra getStatus() {
        return status;
    }

    public void setStatus(StatusQuadra status) {
        this.status = status;
    }
}
