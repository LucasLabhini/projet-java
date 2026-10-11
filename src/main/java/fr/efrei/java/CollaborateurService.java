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

    public java.util.List<Collaborateur> trouverParSalaireSuperieurA(double seuil) {
        try (jakarta.persistence.EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                            "select c from Collaborateur c where c.salaire > :seuil",
                            Collaborateur.class)
                    .setParameter("seuil", seuil)
                    .getResultList();
        }
    }

    public java.util.List<Collaborateur> trouverParNomContenant(String fragment) {
        try (jakarta.persistence.EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                            "select c from Collaborateur c where lower(c.nom) like :motif",
                            Collaborateur.class)
                    .setParameter("motif", "%" + fragment.toLowerCase() + "%")
                    .getResultList();
        }
    }
}