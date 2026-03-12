public class Client {
    private int id;
    private String name;
    private String Surname;
    private String telephone;
    private String email;

    public Client(int id, String name, String Surname, String telephone, String email) {
        this.id = id;
        this.name = name;
        this.Surname = Surname;
        this.telephone = telephone;
        this.email = email;
    }

    public void afficherDetails() {
        System.out.println("ID: " + this.id);
        System.out.println("Name: " + this.name);
        System.out.println("Surname: " + this.Surname);
        System.out.println("Telephone: " + this.telephone);
        System.out.println("Email: " + this.email);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

}
