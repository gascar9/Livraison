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

    public void afficherDetails() {
        System.out.println("┌─────────────────────────────┐");
        System.out.printf("│ 👤 Client #%-17d │%n", id);
        System.out.printf("│ Nom:       %-17s │%n", nom + " " + prenom);
        System.out.printf("│ Téléphone: %-17s │%n", telephone);
        System.out.printf("│ Voiture:     %-17s │%n", voiture);
        System.out.printf("│ Plaque:   %-17s │%n", plaque);
        System.out.println("└─────────────────────────────┘");
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
