package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Dispensaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @NonNull
    @NotBlank
    @Size(max = 255)
    @Column(length = 255, nullable = false)
    private String nom;

    @Size(max = 500)
    @Column(length = 500)
    private String adresse;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String region;

    @ToString.Exclude
    @OneToMany(mappedBy = "dispensaire", cascade = CascadeType.ALL)
    private List<Commande> commandes = new LinkedList<>();
}
