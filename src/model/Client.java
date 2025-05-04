package model;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String passwordHash;

    public int getId() {
        return id; }
    public void setId(int id) {
        this.id = id; }

    public String getNom() {
        return nom; }
    public void setNom(String nom) {
        this.nom = nom; }

    public String getPrenom() {
        return prenom; }
    public void setPrenom(String prenom) {
        this.prenom = prenom; }

    public String getEmail() {
        return email; }
    public void setEmail(String email) {
        this.email = email; }

    public String getPasswordHash() {
        return passwordHash; }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash; }
}
