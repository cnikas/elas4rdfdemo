package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.HashSet;

public class SchemaNode {
    private int count;
    private HashSet<SchemaAdjacency> adjacencies;

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

    public SchemaNode(){
        this.count = 0;
        this.adjacencies = new HashSet<SchemaAdjacency>();
    }
}
