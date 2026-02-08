package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pharmacie.entity.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
public class RepositoryConstraintsAndQueriesTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Test
    public void testDeleteCategorieWithMedicamentsShouldFail() {
        // Attempt to delete a category that has medicines (data.sql has categories with meds)
        assertThrows(Exception.class, () -> {
            categorieRepository.deleteById(1);
            categorieRepository.flush();
        });
    }

    @Test
    public void testDeleteEmptyCategorieShouldSucceed() {
        // Create and delete an empty category
        Categorie c = new Categorie();
        c.setLibelle("TempCatForDeleteTest");
        categorieRepository.saveAndFlush(c);
        Integer id = c.getCode();
        assertTrue(categorieRepository.findById(id).isPresent());
        categorieRepository.deleteById(id);
        categorieRepository.flush();
        assertFalse(categorieRepository.findById(id).isPresent());
    }

    @Test
    public void testDeleteCommandeDeletesLignes() {
        Dispensaire d = new Dispensaire();
        d.setNom("Disp Test");
        d.setRegion("TestRegion");
        dispensaireRepository.saveAndFlush(d);

        Medicament m = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();

        Commande cmd = new Commande();
        cmd.setDispensaire(d);
        LigneCommande l1 = new LigneCommande();
        l1.setCommande(cmd);
        l1.setMedicament(m);
        l1.setQuantite(5);
        l1.setPrixUnitaire(m.getPrixUnitaire());
        cmd.getLignes().add(l1);

        commandeRepository.saveAndFlush(cmd);
        Integer cmdId = cmd.getId();

        // Ensure lines exist
        assertTrue(ligneCommandeRepository.findAll().stream().anyMatch(l -> l.getCommande().getId().equals(cmdId)));

        // Delete command
        commandeRepository.deleteById(cmdId);
        commandeRepository.flush();

        assertFalse(commandeRepository.findById(cmdId).isPresent());
        assertTrue(ligneCommandeRepository.findAll().stream().noneMatch(l -> l.getCommande() != null && cmdId.equals(l.getCommande().getId())));
    }

    @Test
    public void testDeleteDispensaireDeletesCommandesAndLignes() {
        Dispensaire d = new Dispensaire();
        d.setNom("DispForDeleteTest");
        d.setRegion("R");

        Medicament m = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();

        Commande cmd = new Commande();
        cmd.setDispensaire(d);
        LigneCommande l = new LigneCommande();
        l.setCommande(cmd);
        l.setMedicament(m);
        l.setQuantite(2);
        cmd.getLignes().add(l);

        // maintain bidirectional relationship: add command to dispensaire before saving parent
        d.getCommandes().add(cmd);
        dispensaireRepository.saveAndFlush(d); // cascades to commandes and lignes

        Integer cmdId = cmd.getId();
        Integer dispId = d.getId();

        // Reload managed instance and delete parent which has its commandes populated
        Dispensaire managed = dispensaireRepository.findById(dispId).orElseThrow();
        managed.getCommandes().size();
        dispensaireRepository.delete(managed);
        dispensaireRepository.flush();

        assertFalse(dispensaireRepository.findById(dispId).isPresent());
        assertFalse(commandeRepository.findById(cmdId).isPresent());
        assertTrue(ligneCommandeRepository.findAll().stream().noneMatch(li -> li.getCommande() != null && cmdId.equals(li.getCommande().getId())));
    }

    @Test
    public void testQueriesSumAndEnCoursAndAvailableMed() {
        Dispensaire d = new Dispensaire();
        d.setNom("DispQueryTest");
        d.setRegion("Q");
        dispensaireRepository.saveAndFlush(d);

        Medicament m1 = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();
        Medicament m2 = medicamentRepository.findByNom("Morphine 10mg").orElseThrow();

        // Commande expédiée
        Commande sent = new Commande();
        sent.setDispensaire(d);
        sent.setExpedie(true);
        LigneCommande ls1 = new LigneCommande();
        ls1.setCommande(sent);
        ls1.setMedicament(m1);
        ls1.setQuantite(7);
        sent.getLignes().add(ls1);
        commandeRepository.saveAndFlush(sent);

        // Commande non expédiée
        Commande pending = new Commande();
        pending.setDispensaire(d);
        pending.setExpedie(false);
        LigneCommande lp = new LigneCommande();
        lp.setCommande(pending);
        lp.setMedicament(m2);
        lp.setQuantite(4);
        pending.getLignes().add(lp);
        commandeRepository.saveAndFlush(pending);

        Long sum = ligneCommandeRepository.sumQuantitesExpedieesParDispensaire(d.getId());
        assertEquals(7L, sum.longValue());

        List<Commande> enCours = commandeRepository.findEnCoursByDispensaireId(d.getId());
        assertTrue(enCours.stream().anyMatch(c -> c.getId().equals(pending.getId())));
        assertFalse(enCours.stream().anyMatch(c -> c.getId().equals(sent.getId())));

        Integer catCode = m1.getCategorie().getCode();
        List<Medicament> disponibles = medicamentRepository.findAvailableByCategorieCode(catCode);
        assertTrue(disponibles.stream().allMatch(mm -> !mm.isIndisponible() && mm.getUnitesEnStock() >= mm.getUnitesCommandees()));
    }

}

