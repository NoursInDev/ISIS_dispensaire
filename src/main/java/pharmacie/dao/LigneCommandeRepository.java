package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.LigneCommande;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Integer> {
    // Basic repository for ligne de commande; custom queries can be added as needed
}
