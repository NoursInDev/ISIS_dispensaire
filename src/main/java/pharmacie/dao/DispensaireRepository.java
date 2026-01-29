package pharmacie.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Dispensaire;

public interface DispensaireRepository extends JpaRepository<Dispensaire, Integer> {
    // Find all dispensaires in a given region (case-insensitive)
    List<Dispensaire> findByRegionIgnoreCase(String region);
}
