package pharmacie.dao;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    // Find all commandes created after the given date/time
    List<Commande> findByDateCreationAfter(LocalDateTime date);
}
