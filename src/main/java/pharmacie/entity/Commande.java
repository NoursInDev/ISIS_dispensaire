package pharmacie.entity;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import pharmacie.entity.Dispensaire;
import pharmacie.entity.LigneCommande;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private boolean expedie = false;

    @ManyToOne(optional = false)
    @NotNull
    @ToString.Exclude
    private Dispensaire dispensaire;

    @ToString.Exclude
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<LigneCommande> lignes = new LinkedList<>();
}
