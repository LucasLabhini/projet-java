package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.exception.OperationTicketInvalideException;
import fr.efrei.collaborateurs.exception.TicketIntrouvableException;
import fr.efrei.collaborateurs.modele.Collaborateur;
import fr.efrei.collaborateurs.modele.Programmeur;
import fr.efrei.collaborateurs.modele.Testeur;
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
                throw new TicketIntrouvableException( ticketId );
            }
            Collaborateur c = em.find(Collaborateur.class, collaborateurId);
            if (c == null) {
                throw new CollaborateurIntrouvableException(collaborateurId);
            }
            if (!(c instanceof Programmeur)) {
                throw new OperationTicketInvalideException("Un ticket ne peut être assigné qu'à un Programmeur (ce collaborateur est un " + c.getMetier() + ")");
            }
            if (t.getStatut() != Ticket.Statut.A_FAIRE) {
                throw new OperationTicketInvalideException("Un ticket ne peut être assigné que lorsqu'il est à l'état À FAIRE");
            }
            t.setAssigne(c);
            return null;
        });
    }

    public Ticket.Statut demarrerTicket(Long id_ticket) {
        return dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }

            if (t.getAssigne() != null) {
                Long nbEnCours = em.createQuery(
                                "select count(t) from Ticket t where t.assigne.identifiant = :id and t.statut = :statut", Long.class)
                        .setParameter("id", t.getAssigne().getIdentifiant())
                        .setParameter("statut", Ticket.Statut.EN_COURS)
                        .getSingleResult();

                if (nbEnCours >= 3) {
                    throw new OperationTicketInvalideException("Le programmeur " + t.getAssigne().getIdentifiant() + " a déjà 3 tickets en cours.");
                }
            }

            Ticket.Statut avant = t.getStatut();
            t.demarrer();
            logger.debug("Statut de {} : {} -> {}", id_ticket, avant, t.getStatut());
            return t.getStatut();
        });
    }

    public Ticket.Statut demanderValidationTicket(Long id_ticket) {
        return dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }
            Ticket.Statut avant = t.getStatut();
            t.demanderValidation();
            logger.debug("Statut de {} : {} -> {}", id_ticket, avant, t.getStatut());
            return t.getStatut();
        });
    }

    public Ticket.Statut terminerTicket(Long id_ticket, String testeurId) {
        return dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }
            Collaborateur c = em.find(Collaborateur.class, testeurId);
            if (c == null) {
                throw new CollaborateurIntrouvableException(testeurId);
            }
            if (!(c instanceof Testeur)) {
                throw new OperationTicketInvalideException("Seul un Testeur peut valider un ticket (ce collaborateur est un " + c.getMetier() + ")");
            }

            Ticket.Statut avant = t.getStatut();
            t.terminer();
            logger.debug("Statut de {} : {} -> {}", id_ticket, avant, t.getStatut());
            return t.getStatut();
        });
    }

    public Ticket.Statut renvoyerTicket(Long id_ticket, String testeurId) {
        return dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }
            Collaborateur c = em.find(Collaborateur.class, testeurId);
            if (c == null) {
                throw new CollaborateurIntrouvableException(testeurId);
            }
            if (!(c instanceof Testeur)) {
                throw new OperationTicketInvalideException("Seul un Testeur peut renvoyer un ticket (ce collaborateur est un " + c.getMetier() + ")");
            }

            Ticket.Statut avant = t.getStatut();
            t.renvoyerEnCours();
            logger.debug("Statut de {} : {} -> {}", id_ticket, avant, t.getStatut());
            return t.getStatut();
        });
    }

    public void modifierTitreTicket(Long id_ticket, String nouveauTitre) {
        dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }
            t.modifierTitre(nouveauTitre);
            return null;
        });
    }

    public void modifierPrioriteTicket(Long id_ticket, Ticket.Priorite nouvellePriorite) {
        dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }
            t.modifierPriorite(nouvellePriorite);
            return null;
        });
    }

    public void supprimerTicket(Long id_ticket) {
        dansUneTransaction(em -> {
            Ticket t = em.find(Ticket.class, id_ticket);
            if (t == null) {
                throw new TicketIntrouvableException( id_ticket );
            }
            if (t.getStatut() != Ticket.Statut.A_FAIRE) {
                throw new OperationTicketInvalideException("Un ticket ne peut être supprimé que s'il est encore à l'état 'À faire'.");
            }
            em.remove(t);
            return null;
        });
    }

    // ---------- Lectures ----------
    public List<Ticket> tousLesTickets() {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery("select t from Ticket t", Ticket.class).getResultList();
        }
    }

    public void verifierExistenceTicket(Long id_Ticket) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            if (em.find(Ticket.class, id_Ticket) == null) {
                throw new TicketIntrouvableException(id_Ticket);
            }
        }
    }

    public List<Ticket> ticketsNonAssignes() {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery(
                    "SELECT t FROM Ticket t WHERE t.assigne IS NULL ORDER BY t.priorite DESC, t.id ASC",
                    Ticket.class
            ).getResultList();
        }
    }

    public List<Ticket> ticketsParStatut(Ticket.Statut statut) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery("select t from Ticket t where t.statut = :statut", Ticket.class)
                    .setParameter("statut", statut)
                    .getResultList();
        }
    }

    public List<Ticket> ticketsParCollaborateur(String collaborateurId) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery("select t from Ticket t where t.assigne.identifiant = :id", Ticket.class)
                    .setParameter("id", collaborateurId)
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
                    logger.debug("Transaction annulée (rollback) : {}", e.toString());
                }
                throw e;
            }
        }
    }
}