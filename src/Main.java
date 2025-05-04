import service.BanqueService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BanqueService banqueService = new BanqueService();
        Scanner scanner = new Scanner(System.in);
        int choix;

        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Inscription / Connexion client");
            System.out.println("2. Ajouter un compte");
            System.out.println("3. Afficher les comptes");
            System.out.println("4. Effectuer un virement");
            System.out.println("5. Appliquer intérêt pour épargne");
            System.out.println("6. afficher liste clients");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            choix = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            switch (choix) {
                case 1 -> banqueService.connexionOuInscription(scanner);
                case 2 -> banqueService.ajouterCompte(scanner);
                //case 3 -> banqueService.afficherComptes();
                case 4 -> banqueService.effectuerVirement();
                //case 5 -> banqueService.appliquerInteretEpargne();
                case 6 -> banqueService.afficherClients();
                case 0 -> System.out.println("Au revoir !");
                default -> System.out.println("Choix invalide !");
            }

        } while (choix != 0);

        scanner.close();
    }
}