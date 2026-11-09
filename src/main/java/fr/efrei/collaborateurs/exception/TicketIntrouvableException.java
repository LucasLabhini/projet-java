package fr.efrei.collaborateurs.exception;

/** Aucun ticket ne correspond à cet ID en base de données. */
public class TicketIntrouvableException extends RuntimeException {

    private final Long idTicket;

    public TicketIntrouvableException(Long idTicket) {
        super("Aucun ticket avec l'ID " + idTicket + " n'a été trouvé.");
        this.idTicket = idTicket;
    }

    public Long idTicket() { return idTicket; }
}