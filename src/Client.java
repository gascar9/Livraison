public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private static int compteur = 0;

    public Client(String nom, String prenom, String telephone, String email, String adresse) {
        this.id = compteur++;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    public void afficherDetails() {
        System.out.println("┌─────────────────────────────┐");
        System.out.printf("│ 👤 Client #%-17d │%n", id);
        System.out.printf("│ Nom:       %-17s │%n", nom + " " + prenom);
        System.out.printf("│ Téléphone: %-17s │%n", telephone);
        System.out.printf("│ Email:     %-17s │%n", email);
        System.out.printf("│ Adresse:   %-17s │%n", adresse);
        System.out.println("└─────────────────────────────┘");
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getSurname() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
