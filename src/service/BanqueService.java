package service;

import model.Client;
import model.Compte;
import model.CompteCourant;
import model.CompteEpargne;
import util.DBConnection;
import util.PasswordUtils;

import java.sql.*;
import java.util.Scanner;

public class BanqueService {
    private Connection connection;
    public void connexionOuInscription(Scanner scanner) {
        System.out.println("1. Connexion");
        System.out.println("2. Inscription");
        int choix = scanner.nextInt();
        scanner.nextLine();  // Consume the newline

        if (choix == 1) {
            System.out.print("Email : ");
            String email = scanner.nextLine();
            System.out.print("Mot de passe : ");
            String password = scanner.nextLine();
            Client client = connexionClient(email, password);
            if (client != null) {
                System.out.println("Connexion réussie.");
            } else {
                System.out.println("Échec de la connexion.");
            }
        } else if (choix == 2) {
            System.out.print("Nom : ");
            String nom = scanner.nextLine();
            System.out.print("Prénom : ");
            String prenom = scanner.nextLine();
            System.out.print("Email : ");
            String email = scanner.nextLine();
            System.out.print("Mot de passe : ");
            String password = scanner.nextLine();

            Client client = new Client();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setPasswordHash(PasswordUtils.hashPassword(password));

            inscrireClient(client);
            System.out.println("Inscription réussie !");
        } else {
            System.out.println("Choix invalide.");
        }
    }

    public Client connexionClient(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM client WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                if (passwordHash.equals(PasswordUtils.hashPassword(password))) {
                    Client c = new Client();
                    c.setId(rs.getInt("id"));
                    c.setNom(rs.getString("nom"));
                    c.setPrenom(rs.getString("prenom"));
                    c.setEmail(rs.getString("email"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void inscrireClient(Client client) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO client(nom, prenom, email, password_hash) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPasswordHash());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherClients() {
        System.out.println("Liste des clients :");
        String sql = "SELECT id, nom, prenom, email FROM client";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");

                System.out.println("ID: " + id + ", Nom: " + nom + ", Prénom: " + prenom + ", Email: " + email);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des clients : " + e.getMessage());
        }
    }
    public enum TypeCompte {
        COURANT, EPARGNE
    }
    public void ajouterCompte(Scanner scanner) {
        System.out.println("1. Compte Courant");
        System.out.println("2. Compte Epargne");
        System.out.print("Choix du type de compte : ");
        int choixCompte = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            System.out.print("Saisir le numéro de compte : ");
            String  numeroCompte = scanner.nextLine();

            System.out.print("ID du client : ");
            int idClient = scanner.nextInt();

            System.out.print("Solde initial : ");
            double solde = scanner.nextDouble();



            // Déterminer le type de compte via enum
            String type;
            if (choixCompte == 1) {
                type = TypeCompte.COURANT.name();
            } else if (choixCompte == 2) {
                type = TypeCompte.EPARGNE.name();
            } else {
                System.out.println("Choix invalide.");
                return;
            }

            // Insertion dans la table 'compte' avec le champ 'type'
            String sqlCompte = "INSERT INTO compte (solde, id_client, type , numero_compte) VALUES (?, ?, ?, ?)";
            PreparedStatement psCompte = conn.prepareStatement(sqlCompte, Statement.RETURN_GENERATED_KEYS);
            psCompte.setDouble(1, solde);
            psCompte.setInt(2, idClient);
            psCompte.setString(3, type);
            psCompte.setString(4, numeroCompte);
            psCompte.executeUpdate();

            ResultSet rs = psCompte.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                throw new SQLException("Échec récupération ID compte");
            }

            int compteId = rs.getInt(1);

            // Insertion dans les tables spécifiques selon le type de compte
            if (choixCompte == 1) {
                System.out.print("Découvert autorisé : ");
                double decouvert = scanner.nextDouble();

                String sqlCourant = "INSERT INTO compte_courant (id, decouvert) VALUES (?, ?)";
                PreparedStatement psC = conn.prepareStatement(sqlCourant);
                psC.setInt(1, compteId);
                psC.setDouble(2, decouvert);
                psC.executeUpdate();

            } else if (choixCompte == 2) {
                System.out.print("Taux d'intérêt : ");
                double taux = scanner.nextDouble();

                String sqlEpar = "INSERT INTO compte_epargne (id, taux_interet) VALUES (?, ?)";
                PreparedStatement psE = conn.prepareStatement(sqlEpar);
                psE.setInt(1, compteId);
                psE.setDouble(2, taux);
                psE.executeUpdate();
            }

            conn.commit();
            System.out.println("Compte ajouté en base avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }







    public void effectuerVirement() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez le numero de compte source : ");
        int numeroSource = scanner.nextInt();

        System.out.print("Entrez le numéro du compte destination : ");
        int numeroDestination = scanner.nextInt();

        System.out.print("Entrez le montant à transférer : ");
        double montant = scanner.nextDouble();

        // Vérifier que les deux comptes existent
        try {
            if (compteExiste(numeroSource) && compteExiste(numeroDestination)) {
                // Vérifier que le compte source a suffisamment de fonds
                double soldeSource = getSoldeCompte(numeroSource);
                if (soldeSource >= montant) {
                    // Effectuer le virement
                    effectuerVirementSQL(numeroSource, numeroDestination, montant);
                    System.out.println("Virement effectué avec succès !");
                } else {
                    System.out.println("Solde insuffisant sur le compte source.");
                }
            } else {
                System.out.println("L'un des comptes n'existe pas.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du virement : " + e.getMessage());
        }
    }

    private boolean compteExiste(int numeroCompte) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.out.println("La connexion à la base de données a échoué.");
                return false;
            }

            String query = "SELECT COUNT(*) FROM compte WHERE numero_compte = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, numeroCompte);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    // Récupère le solde d'un compte
    private double getSoldeCompte(int numeroCompte) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.out.println("La connexion à la base de données a échoué.");
                return 0;
            }

            String query = "SELECT solde FROM compte WHERE numero_compte = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, numeroCompte);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getDouble("solde");
                }
            }
        }
        return 0;
    }
    // Effectue le virement en mettant à jour les soldes des comptes
    private void effectuerVirementSQL(int numeroSource, int numeroDestination, double montant) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                throw new SQLException("La connexion à la base de données n'a pas pu être établie.");
            }

            connection.setAutoCommit(false);

            String updateSource = "UPDATE compte SET solde = solde - ? WHERE numero_compte = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateSource)) {
                statement.setDouble(1, montant);
                statement.setInt(2, numeroSource);
                statement.executeUpdate();
            }

            String updateDestination = "UPDATE compte SET solde = solde + ? WHERE numero_compte = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateDestination)) {
                statement.setDouble(1, montant);
                statement.setInt(2, numeroDestination);
                statement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors du virement, transaction annulée.", e);
        }
    }
}