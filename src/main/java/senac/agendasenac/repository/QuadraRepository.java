package senac.agendasenac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.agendasenac.entity.Quadra;

public interface QuadraRepository extends JpaRepository<Quadra, Long> {

    boolean existsByNome(String nome);
}
