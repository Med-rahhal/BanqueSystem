package model;


public class CompteCourant extends Compte {
    private double decouvert;

    @Override
    public void appliquerInteret() {
        System.out.println("Aucun intérêt pour le compte courant.");
    }

    public double getDecouvert() {
        return decouvert; }
    public void setDecouvert(double decouvert) {
        this.decouvert = decouvert; }
}