package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.ArrayList;

/*
 * Container class for the list of loaded triples and the total number of triples found.
 *
 */
public class TriplesContainer {
    private int maxSize;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public ArrayList<ResultTriple> getTriples() {
        return triples;
    }

    public void setTriples(ArrayList<ResultTriple> triples) {
        this.triples = triples;
    }

    private ArrayList<ResultTriple> triples;
}
