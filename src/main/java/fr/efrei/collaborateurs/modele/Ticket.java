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

    public void renvoyerEnCours() {
        if (this.statut != Statut.A_VALIDER) {
            throw new IllegalStateException("Seul un ticket à l'état 'À valider' peut être renvoyé en cours.");
        }
        this.statut = Statut.EN_COURS;
    }

    public void modifierTitre(String nouveauTitre) {
        if (this.statut == Statut.TERMINE) {
            throw new IllegalStateException("Un ticket terminé ne peut plus être modifié.");
        }
        if (nouveauTitre == null || nouveauTitre.isBlank()) {
            throw new IllegalArgumentException("Le titre du ticket ne peut pas être vide.");
        }
        this.titre = nouveauTitre.trim();
    }

    public void modifierPriorite(Priorite nouvellePriorite) {
        if (this.statut == Statut.TERMINE) {
            throw new IllegalStateException("Un ticket terminé ne peut plus être modifié.");
        }
        if (nouvellePriorite == null ) {
            throw new IllegalArgumentException("La priorité du ticket ne peut pas être vide.");
        }
        this.priorite = nouvellePriorite;
    }

    @Override
    public String toString() {
        String responsable = (assigne != null) ? assigne.getPrenom() + " " + assigne.getNom() : "aucun";
        return "Ticket #" + id + " : " + titre + " (" + priorite + ") - " + statut + " - Responsable : " + responsable;
    }


}