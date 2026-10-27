package fr.efrei.collaborateurs.modele;

import fr.efrei.collaborateurs.modele.Collaborateur;
import jakarta.persistence.*;


@Entity
@Table(name = "ticket")
public class Ticket {

    public enum Statut {
        A_FAIRE,
        EN_COURS,
        A_VALIDER,
        TERMINE
    }

    public enum Priorite {
        BASSE,
        NORMALE,
        HAUTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Enumerated(EnumType.STRING)
    private Priorite priorite;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "assigne_id")
    private Collaborateur assigne;

    protected Ticket() {
        // exigé par JPA, comme pour vos autres entités
    }

    public Ticket(String titre, Priorite priorite, Collaborateur assigne) {
        this.titre = titre;
        this.priorite = priorite;
        this.statut = Statut.A_FAIRE;
        this.assigne = assigne;
    }

    public void setTitre (String nouveauTitre){
        this.titre = nouveauTitre;
    }

    public void setPriorite (Priorite nouvellePriorite){
        this.priorite = nouvellePriorite;
    }
    // Constructeur public, accesseurs et autres éléments utiles à compléter. }
}