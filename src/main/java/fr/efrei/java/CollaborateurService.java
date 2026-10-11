package fr.efrei.java;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class CollaborateurService {
    private final EntityManagerFactory fabrique;

    public CollaborateurService(EntityManagerFactory fabrique) {
        this.fabrique = fabrique;
    }

    public void ajouter(Collaborateur collaborateur) {
        if (trouver(collaborateur.getIdentifiant()) != null) {
            throw new CollaborateurDejaExistantException("Le collaborateur " + collaborateur.getIdentifiant() + " existe déjà !");
        }

        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                em.persist(collaborateur);
                transaction.commit();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }
    public Collaborateur trouver(String identifiant) {
        try (EntityManager entityManager = fabrique.createEntityManager()) {
            return entityManager.find(Collaborateur.class, identifiant);
        }
    }

    public void augmenterAlice() {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin(); // Début de la transaction

            try {
                Collaborateur alice = em.find(Collaborateur.class, "C001");

                if (alice != null) {
                    alice.augmenterSalaire(10.0);
                }
                transaction.commit();

            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }
}