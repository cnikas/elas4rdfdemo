package gr.forth.ics.isl.elas4rdfdemo.models;

public class ParsedSyntax {
    private String subject;
    private String predicate;
    private String object;

    public ParsedSyntax() {
        this.subject = "";
        this.predicate = "";
        this.object = "";

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "subject: " + this.getSubject() + " predicate: " + this.getPredicate() + " object: " + this.getObject();
    }
}
