package pharmacie.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import pharmacie.entity.Medicament;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(optional = false)
    @NotNull
    @ToString.Exclude
    private Commande commande;

    @ManyToOne(optional = false)
    @NotNull
    @ToString.Exclude
    private Medicament medicament;

    @PositiveOrZero
    private int quantite = 0;

    @PositiveOrZero
    private BigDecimal prixUnitaire = BigDecimal.ZERO;
}
