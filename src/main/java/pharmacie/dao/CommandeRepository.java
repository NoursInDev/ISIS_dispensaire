package pharmacie.dao;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pharmacie.entity.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    // Find all commandes created after the given date/time
    List<Commande> findByDateCreationAfter(LocalDateTime date);

    // Trouve les commandes en cours (non expédiées) pour un dispensaire donné
    @Query("SELECT c FROM Commande c WHERE c.dispensaire.id = :dispId AND c.expedie = false")
    List<Commande> findEnCoursByDispensaireId(@Param("dispId") Integer dispensaireId);
}
