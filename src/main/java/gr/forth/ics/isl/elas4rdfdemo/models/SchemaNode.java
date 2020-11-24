package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.HashSet;

public class SchemaNode {
    private int count;
    private HashSet<SchemaAdjacency> adjacencies;

    private String name;
    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashSet<SchemaAdjacency> getAdjacencies() {
        return adjacencies;
    }

    public void setAdjacencies(HashSet<SchemaAdjacency> adjacencies) {
        this.adjacencies = adjacencies;
    }

    public SchemaNode() {
        this.count = 0;
        this.adjacencies = new HashSet<SchemaAdjacency>();
    }

    public SchemaNode(FrequentItem fi) {
        this.setUri(fi.getUri());
        this.setName(fi.getName());
        this.setCount(fi.getCount());
        this.setAdjacencies(new HashSet<SchemaAdjacency>());
    }

    public void addAdjacency(SchemaAdjacency adj) {
        this.adjacencies.add(adj);
    }
}
