package fr.efrei.collaborateurs.modele;

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

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    private Priorite priorite;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "assigne_id")
    private Collaborateur assigne;

    protected Ticket() {
        // Exigé par JPA
    }

    public Ticket(String titre, Priorite priorite) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre du ticket ne peut pas être vide");
        }
        this.titre = titre.trim();
        this.priorite = priorite;
        this.statut = Statut.A_FAIRE;
        this.assigne = null;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public Priorite getPriorite() { return priorite; }
    public Statut getStatut() { return statut; }
    public Collaborateur getAssigne() { return assigne; }

    public void setAssigne(Collaborateur assigne) {
        this.assigne = assigne;
    }

    @Override
    public String toString() {
        String resp = (assigne != null) ? assigne.getPrenom() + " " + assigne.getNom() : "Non assigné";
        return String.format("#%d [%s] %s (%s) - Responsable : %s", id, priorite, titre, statut, resp);
    }
}