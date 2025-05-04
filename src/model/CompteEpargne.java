package model;

public class CompteEpargne extends Compte {
    private double tauxInteret;

    @Override
    public void appliquerInteret() {
        double interet = getSolde() * tauxInteret;
        setSolde(getSolde() + interet);
        System.out.println("Les intérêts ont été appliqués sur le compte épargne.");
    }

    public double getTauxInteret() {
        return tauxInteret; }
    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret; }
}