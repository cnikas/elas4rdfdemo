package gr.forth.ics.isl.elas4rdfdemo.qa.models;

import java.util.List;
import java.util.Objects;

public class DBPediaOntologyUri {
    private String uri;
    private String label;
    private String comment;
    private List<String> equivalentClasses;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEquivalentClasses() {
        return equivalentClasses;
    }

    public void setEquivalentClasses(List<String> equivalentClasses) {
        this.equivalentClasses = equivalentClasses;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String types) {
        this.comment = types;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DBPediaOntologyUri() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBPediaOntologyUri that = (DBPediaOntologyUri) o;
        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
