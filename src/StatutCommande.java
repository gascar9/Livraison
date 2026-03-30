public enum StatutCommande {
    EN_ATTENTE("En attente"),
    EN_PREPARATION("En preparation"),
    EN_LIVRAISON("En livraison"),
    LIVREE("Livree");

    private final String label;

    StatutCommande(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
