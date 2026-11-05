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

    public void demarrer() {
        if (this.statut != Statut.A_FAIRE) {
            throw new IllegalStateException("Seul un ticket À FAIRE peut être démarré.");
        }
        if (this.assigne == null) {
            throw new IllegalStateException("Impossible de démarrer un ticket sans responsable.");
        }
        this.statut = Statut.EN_COURS;
    }

    public void demanderValidation() {
        if (this.statut != Statut.EN_COURS) {
            throw new IllegalStateException("Le ticket doit être EN_COURS pour demander une validation.");
        }
        this.statut = Statut.A_VALIDER;
    }

    public void terminer() {
        if (this.statut != Statut.A_VALIDER) {
            throw new IllegalStateException("Le ticket doit d'abord être validé avant d'être terminé.");
        }
        this.statut = Statut.TERMINE;
    }

    @Override
    public String toString() {
        String responsable = (assigne != null) ? assigne.getPrenom() + " " + assigne.getNom() : "aucun";
        return "Ticket #" + id + " : " + titre + " (" + priorite + ") - " + statut + " - Responsable : " + responsable;
    }


}