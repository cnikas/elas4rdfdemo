package gr.forth.ics.isl.elas4rdfdemo.models;

public class SchemaAdjacency {
    private String label;
    private String nodeTo;

    public SchemaAdjacency(String label, String nodeTo) {
        this.label = label;
        this.nodeTo = nodeTo;
    }

    public String getLabel() {
        return label;
    }

    public String getNodeTo() {
        return nodeTo;
    }

    @Override
    public int hashCode() {
        return label.hashCode() ^ nodeTo.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SchemaAdjacency)) return false;
        SchemaAdjacency other = (SchemaAdjacency) o;
        return this.label.equals(other.getLabel()) &&
                this.nodeTo.equals(other.getNodeTo());
    }
}
