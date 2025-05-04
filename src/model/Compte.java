package model;

public abstract class Compte {
    private int id;
    private double solde;

    public abstract void appliquerInteret();

    public int getId() {
        return id; }
    public void setId(int id) {
        this.id = id; }

    public double getSolde() {
        return solde; }
    public void setSolde(double solde) {
        this.solde = solde; }
}
