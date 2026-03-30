public enum TypeLivraison {
    STANDARD("Standard"),
    EXPRESS("Express");

    private final String label;

    TypeLivraison(String label) {
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
