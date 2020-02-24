package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.HashSet;

public class Query {
    private String query;
    private HashSet<String> firstTermKeywords;
    private HashSet<String> predicateKeywords;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HashSet<String> getFirstTermKeywords() {
        return firstTermKeywords;
    }

    public void setFirstTermKeywords(HashSet<String> firstTermKeywords) {
        this.firstTermKeywords = firstTermKeywords;
    }

    public HashSet<String> getPredicateKeywords() {
        return predicateKeywords;
    }

    public void setPredicateKeywords(HashSet<String> predicateKeywords) {
        this.predicateKeywords = predicateKeywords;
    }

    public Query() {
    }
}
