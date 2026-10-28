package fr.efrei.collaborateurs;

import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.TicketService;
import fr.efrei.collaborateurs.ui.ConsoleApp;

/** Point d'entrée : il assemble les morceaux, rien de plus. */
public class HelloEfrei {

    public static void main(String[] args) {
        CollaborateurService service = new CollaborateurService(JpaUtil.initEMFactory());
        TicketService service2 = new TicketService(JpaUtil.initEMFactory());

        try {
            new ConsoleApp(service, service2).lancer();
        } finally {
            JpaUtil.fermerEMFactory();
        }
    }
}
