public class Livreur {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String voiture;
    private String plaque;

    public Livreur(int id, String nom, String prenom, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getVoiture() {
        return voiture;
    }
    public void setVoiture(String voiture) {
        this.voiture = voiture;
    }
    public String getPlaque() {
        return plaque;
    }
    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }

}
