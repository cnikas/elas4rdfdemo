package gr.forth.ics.isl.elas4rdfdemo.qa.models;

public class LabeledUri {
    String label;
    String uri;

    public LabeledUri(String label, String uri) {
        this.label = label;
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        LabeledUri other = (LabeledUri)obj;
        return this.getUri().equals(other.getUri());
    }
}
