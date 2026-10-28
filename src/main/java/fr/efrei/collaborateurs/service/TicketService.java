package fr.efrei.collaborateurs.service;

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

    public TicketService(EntityManagerFactory emf) { this.eMFactory = emf; }

    // ---------- Écritures (dans une transaction) ----------

    public void ajouterTicket(Ticket ticket) {
        dansUneTransaction(em -> {
            em.persist(ticket);
            return null;
        });
        logger.info("Ticket enregistré");
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