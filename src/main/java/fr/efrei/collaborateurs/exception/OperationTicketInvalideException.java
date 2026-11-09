package fr.efrei.collaborateurs.exception;

/** Règle métier du ticket violée (ex: changement d'état interdit). */
public class OperationTicketInvalideException extends RuntimeException {

    public OperationTicketInvalideException(String message) {
        super(message);
    }
}