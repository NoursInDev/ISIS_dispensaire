package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pharmacie.entity.LigneCommande;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Integer> {
    // Somme des quantités des lignes pour les commandes expédiées d'un dispensaire
    @Query("SELECT COALESCE(SUM(l.quantite),0) FROM LigneCommande l WHERE l.commande.dispensaire.id = :dispId AND l.commande.expedie = true")
    Long sumQuantitesExpedieesParDispensaire(@Param("dispId") Integer dispensaireId);
}
