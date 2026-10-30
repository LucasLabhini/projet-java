package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.modele.Collaborateur;
import fr.efrei.collaborateurs.modele.Programmeur;
import fr.efrei.collaborateurs.modele.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final EntityManagerFactory eMFactory;

    public TicketService(EntityManagerFactory emf) {
        this.eMFactory = emf;
    }

    // ---------- Écritures (dans une transaction) ----------

    public void ajouterTicket(Ticket ticket) {
        dansUneTransaction(em -> {
            em.persist(ticket);
            return null;
        });
        logger.info("Ticket enregistré");
    }

    public void assignerTicket(Long ticketId, String collaborateurId) {
        dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new IllegalArgumentException("Ticket introuvable (ID: " + ticketId + ")");
            }

            Collaborateur c = em.find(Collaborateur.class, collaborateurId);
            if (c == null) {
                throw new CollaborateurIntrouvableException(collaborateurId);
            }

            if (!(c instanceof Programmeur)) {
                throw new IllegalArgumentException("Un ticket ne peut être assigné qu'à un Programmeur (ce collaborateur est un " + c.getMetier() + ")");
            }

            if (t.getStatut() != Ticket.Statut.A_FAIRE) {
                throw new IllegalArgumentException("Un ticket ne peut être assigné que lorsqu'il est à l'état À FAIRE");
            }

            t.setAssigne(c);
            return null;
        });
    }

    // ---------- Lectures ----------

    public List<Ticket> tousLesTickets() {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery("select t from Ticket t", Ticket.class).getResultList();
        }
    }

    public List<Ticket> ticketsParStatut(Ticket.Statut statut) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery("select t from Ticket t where t.statut = :statut", Ticket.class)
                    .setParameter("statut", statut)
                    .getResultList();
        }
    }

    // ---------- Outils internes ----------

    private <T> T dansUneTransaction(Function<EntityManager, T> travail) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                T resultat = travail.apply(em);
                transaction.commit();
                return resultat;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                    logger.debug("Transaction annulée (rollback) : {}", em.toString());
                }
                throw e;
            }
        }
    }
}