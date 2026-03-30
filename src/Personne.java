public abstract class Personne {
    protected int id;
    private String nom;
    private String prenom;
    private String telephone;

    public Personne(int id, String nom, String prenom, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public abstract void afficherDetails();

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    @Override
    public String toString() {
        return "#" + id + " " + nom + " " + prenom;
    }
}
